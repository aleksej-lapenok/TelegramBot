package ru.ifmo.services.game.tictactoe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.ifmo.services.game.GameUpdate;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Game;
import ru.ifmo.telegram.bot.services.main.Games;
import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Cawa on 02.12.2017.
 */
public class TicTacToeGame<S extends TTTStep> implements Game<S> {
    private Player p1, p2, currPlayer;
    private Board board;
    private GameState state;

    TicTacToeGame(Player player1, Player player2) {
        p1 = player1;
        p2 = player2;
        currPlayer = p1;
        state = GameState.TURN;
        board = new Board();
    }

    TicTacToeGame(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject gameJson = parser.parse(jsonString).getAsJsonObject();
        state = GameState.valueOf(gameJson.get("state").getAsString());
        board = new Board(gameJson.get("board").getAsJsonObject());
        // TODO: players from json
        p1 = null;
        p2 = null;
        currPlayer = null;
    }

    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        object.add("board", board.toJson());
        object.addProperty("state", state.toString());
        // TODO: players to json
//        object.addProperty("p1", p1.toString());
//        object.addProperty("p2", p2.toString());
//        object.addProperty("currPlayer", currPlayer.toString());
        return object;
    }

    private boolean checkWinner() {
        return board.hasThreeInARow();
    }

    @Contract(pure = true)
    private Tile.TileState getState(Player p) {
        return p == p1 ? Tile.TileState.MARK : p == p2 ? Tile.TileState.ZERO : Tile.TileState.EMPTY;
    }

    @Override
    @NotNull
    public String step(@NotNull S step) {
        switch (state) {
            case TURN:
                if (currPlayer == step.player) {
                    Tile.TileState tileState = getState(step.player);
                    if (tileState != Tile.TileState.EMPTY) {
                        if (board.makeTurn(step.x, step.y, tileState)) {
                            if (checkWinner()) {
                                state = GameState.WINNER;
                                return "You won.";
                            }
                            if (board.isFull()) {
                                state = GameState.DRAW;
                                return "You made draw.";
                            }
                            currPlayer = currPlayer == p1 ? p2 : p1;
                        }
                        return "You tried to make wrong turn, try again.";
                    }
                    return "It is not your game, you can't make turns.";
                }
                return "It is not your turn. Wait.";
            case WINNER:
                return "You can't make turns, this game has won by " + currPlayer.getName() + ".";
            case DRAW:
                return "You can't make turns, there is a draw.";
            default:
                return "Chuck?!?!?!";
        }
    }

    @NotNull
    @Override
    public Keyboard getKeyboard(Player p) {
        return board.getKeyboard();
    }


    @Override
    public File drawPicture(@NotNull Player player) {
        return null;
    }

    @NotNull
    @Override
    public GameUpdate getGameUpdate(@NotNull Player player) {
        File f = drawPicture(player);
        switch (state) {
            case TURN:
                if (currPlayer == player) {
                    return new GameUpdate("Make your turn.\n" + board.toString(), board.getKeyboard(), f);
                } else {
                    return new GameUpdate("Wait for opponent's turn.\n" + board.toString(), new Keyboard(), f);
                }
            case WINNER:
                return new GameUpdate("The game has won by " + currPlayer.getName() + "." + board.toString(), board.getKeyboard(), f);
            case DRAW:
                return new GameUpdate("There is a draw." + board.toString(), board.getKeyboard(), f);
            default:
                return new GameUpdate("Chuck?!?!?!" + board.toString(), board.getKeyboard(), f);
        }
    }

    @NotNull
    @Override
    public String getMessage(@NotNull Player player) {
        return getInfo(player);
    }

    private String getInfo(Player player) {
        return getGameUpdate(player).getText();
    }

    @Override
    public void surrender(@NotNull Player player) {
        if (state == GameState.TURN) {
            if (p1 == player || p2 == player) {
                currPlayer = player == p1 ? p2 : p1;
                state = GameState.WINNER;
            }
        }
    }

    @NotNull
    @Override
    public String toJson() {
        return toJsonObject().toString();
    }

    @NotNull
    @Override
    public List<Player> getPlayes() {
        return Arrays.asList(p1, p2);
    }

    @NotNull
    @Override
    public Games getGameId() {
        return Games.TTT;
    }

    @Override
    public boolean isFinished() {
        return state != GameState.TURN;
    }

    public boolean isCurrent(Player p) {
        return p == currPlayer;
    }

    enum GameState {
        DRAW,
        TURN,
        WINNER
    }
}


