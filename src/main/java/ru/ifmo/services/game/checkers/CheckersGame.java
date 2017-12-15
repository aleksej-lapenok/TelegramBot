package ru.ifmo.services.game.checkers;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.services.game.GameUpdate;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Game;
import ru.ifmo.telegram.bot.services.main.Games;
import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CheckersGame<S extends CheckersStep> implements Game<S> {
    private boolean isFirstTurn;
    private int currPlayer;
    private int fromX, fromY;
    private CheckersBoard board;
    private Player player1, player2, winner;

    CheckersGame(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currPlayer = 1;
        this.board = new CheckersBoard();
        isFirstTurn = true;
    }

    private boolean checkWinner() {
        return board.blackCount == 0 || board.whiteCount == 0;
    }

    @NotNull
    @Override
    public String step(@NotNull S step) {
        if (currPlayer == 0) {
            return "Game is won by " + winner.getName();
        }
        if (isFirstTurn) {
            isFirstTurn = false;
            fromX = step.x;
            fromY = step.y;
            return "Continue your turn!";
        }
        isFirstTurn = true;
        Player player = (currPlayer == 1) ? player1 : player2;
        if (player.equals(step.player)) {
            if (board.makeTurn(fromX - 1, fromY - 1, step.x - 1, step.y - 1, currPlayer)) {
                if (checkWinner()) {
                    winner = player;
                    currPlayer = 0;
                    return winner.getName() + " won";
                } else {
                    currPlayer *= -1;
                    return "Turn was made";
                }
            } else {
                return "Wrong turn";
            }
        } else {
            return "Wrong player tried to make turn";
        }
    }

    @NotNull
    @Override
    public String getMessage(@NotNull Player player) {
        if (player.equals(player1) || player.equals(player2)) {
            StringBuilder sb = new StringBuilder();
            if (currPlayer != 0) {
                sb.append("Current player: ");
                sb.append(currPlayer == 1 ? player1.getName() : player2.getName());
            } else {
                if (winner != null) {
                    sb.append("Winner: ");
                    sb.append(winner.getName());
                } else {
                    sb.append("Draw");
                }
            }
            sb.append('\n');
            sb.append(board.toString());
            return sb.toString();
        } else {
            return "Wrong player";
        }
    }

    @NotNull
    @Override
    public String toJson() {
        throw new NotImplementedException();
    }

    @Override
    public void surrender(@NotNull Player player) {
        if (player.equals(player1)) {
            winner = player2;
            currPlayer = 0;
        } else if (player.equals(player2)) {
            winner = player1;
            currPlayer = 0;
        }
    }

    @NotNull
    @Override
    public List<Player> getPlayes() {
        return Arrays.asList(player1, player2);
    }

    @NotNull
    @Override
    public Games getGameId() {
        return Games.CHECKERS;
    }

    @Override
    public boolean isFinished() {
        return currPlayer == 0;
    }

    @NotNull
    @Override
    public Keyboard getKeyboard(@NotNull Player player) {
        return board.getKeyboard();
    }

    @NotNull
    @Override
    public GameUpdate getGameUpdate(@NotNull Player player) {
        return new GameUpdate(getMessage(player), getKeyboard(player), drawPicture(player));
    }

    @Override
    public boolean isCurrent(@NotNull Player player) {
        return (player1.equals(player) && currPlayer > 0) || (player2.equals(player) && currPlayer < 0);
    }

    @NotNull
    @Override
    public File drawPicture(@NotNull Player player) {
        return new File("");
    }
}
