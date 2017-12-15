package ru.ifmo.services.game.tictactoe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kotlin.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.ifmo.services.game.GameException;
import ru.ifmo.services.game.GameUpdate;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Game;
import ru.ifmo.telegram.bot.services.main.Games;
import ru.ifmo.telegram.bot.services.telegramApi.TgException;
import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Cawa on 02.12.2017.
 */
public class TicTacToeGame<S extends TTTStep> implements Game<S> {
    private static final File PICTURES_DIRECTORY = new File(TicTacToeGame.class.getClassLoader().getResource("/tictactoe/images").getFile());
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

    TicTacToeGame(String jsonString, Player player1, Player player2) throws GameException {
        JsonParser parser = new JsonParser();
        JsonObject gameJson = parser.parse(jsonString).getAsJsonObject();
        state = GameState.valueOf(gameJson.get("state").getAsString());
        board = new Board(gameJson.get("board").getAsJsonObject());
        if (gameJson.get("p1").getAsLong() == player1.getId() && gameJson.get("p2").getAsLong() == player2.getId()) {
            p1 = player1;
            p2 = player2;
        } else {
            if (gameJson.get("p2").getAsLong() == player1.getId() && gameJson.get("p1").getAsLong() == player2.getId()) {
                p2 = player1;
                p1 = player2;
            } else {
                throw new GameException("Wrong players for deserialization");
            }
        }
        currPlayer = gameJson.get("currPlayer").getAsLong() == player1.getId() ? player1 : player2;
    }

    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        object.add("board", board.toJson());
        object.addProperty("state", state.toString());
        object.addProperty("p1", p1.getId());
        object.addProperty("p2", p2.getId());
        object.addProperty("currPlayer", currPlayer.getId());
        return object;
    }

    private boolean checkWinner() {
        return board.hasThreeInARow();
    }

    @Contract(pure = true)
    private Tile.TileState getState(Player p) {
        return p.equals(p1) ? Tile.TileState.MARK : p.equals(p2) ? Tile.TileState.ZERO : Tile.TileState.EMPTY;
    }

    @Override
    @NotNull
    public Pair<String, Boolean> step(@NotNull S step) {
        switch (state) {
            case TURN:
                if (currPlayer.equals(step.player)) {
                    Tile.TileState tileState = getState(step.player);
                    if (tileState != Tile.TileState.EMPTY) {
                        if (board.makeTurn(step.x, step.y, tileState)) {
                            if (checkWinner()) {
                                state = GameState.WINNER;
                                return new Pair<>("You won.", true);
                            }
                            if (board.isFull()) {
                                state = GameState.DRAW;
                                return new Pair<>("You made draw.", true);
                            }
                            currPlayer = currPlayer == p1 ? p2 : p1;
                        }
                        return new Pair<>("You made turn.", true);
                    }
                    return new Pair<>("It is not your game, you can't make turns.", false);
                }
                return new Pair<>("It is not your turn. Wait.", false);
            case WINNER:
                return new Pair<>("You can't make turns, this game has won by " + currPlayer.getName() + ".", false);
            case DRAW:
                return new Pair<>("You can't make turns, there is a draw.", false);
            default:
                return new Pair<>("Chuck?!?!?!", false);
        }
    }


    private RenderedImage drawPicture(Player player) throws TgException{
        String picture = board.toString();
        Image crossImage = null;
        Image zeroImage = null;
        Image fieldImage = null;
        try {
            crossImage = ImageIO.read(new File(PICTURES_DIRECTORY, "krest.png"));
            zeroImage = ImageIO.read(new File(PICTURES_DIRECTORY, "nol.png"));
            fieldImage = ImageIO.read(new File(PICTURES_DIRECTORY, "pole.png"));
        } catch (IOException e) {
            throw new TgException("Need game resourses", e);
        }
        BufferedImage image = new BufferedImage(90, 90, BufferedImage.TYPE_INT_ARGB);
        String b[] = picture.split("\\n");
        char a[] = new char[9];
        for (int i = 0; i < b.length; i++) {
            a[i * 3] = b[i].charAt(0);
            a[i * 3 + 1] = b[i].charAt(1);
            a[i * 3 + 2] = b[i].charAt(2);
        }
        Graphics g = image.getGraphics();
        g.drawImage(fieldImage, 0, 0, null);
        for (int i = 0; i < a.length; i++) {
            if ('0' == a[i]) {
                g.drawImage(zeroImage, (i % 3) * 30, (i / 3) * 30, null);
            }
            if (a[i] == 'x') {
                g.drawImage(crossImage, (i % 3) * 30, (i / 3) * 30, null);
            }
        }
        return image;
    }

    @NotNull
    @Override
    public GameUpdate getGameUpdate(@NotNull Player player) throws TgException {
        Raster raster = drawPicture(player).getData();
        DataBufferByte data  = (DataBufferByte) raster.getDataBuffer();
        byte[] f = data.getData();
        switch (state) {
            case TURN:
                if (currPlayer == player) {
                    return new GameUpdate("Make your turn.\n" + board.toString(), board.getKeyboard(), f);
                } else {
                    return new GameUpdate("Wait for opponent's turn.\n" + board.toString(), new Keyboard(), f);
                }
            case WINNER:
                return new GameUpdate("The game has won by " + currPlayer.getName() + ".\n" + board.toString(), new Keyboard(), f);
            case DRAW:
                return new GameUpdate("There is a draw.\n" + board.toString(), new Keyboard(), f);
            default:
                return new GameUpdate("Chuck?!?!?!\n" + board.toString(), new Keyboard(), f);
        }
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
    public List<Player> getPlayers() {
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

    public boolean isCurrent(@NotNull Player p) {
        return p == currPlayer;
    }

    enum GameState {
        DRAW,
        TURN,
        WINNER
    }
}


