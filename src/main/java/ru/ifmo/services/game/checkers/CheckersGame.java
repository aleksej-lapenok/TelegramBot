package ru.ifmo.services.game.checkers;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Game;
import ru.ifmo.telegram.bot.services.main.Games;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;
import java.util.List;

public class CheckersGame<S extends CheckersStep> implements Game<S> {
    private Player player1, player2;
    private CheckersBoard board;
    private int currPlayer;
    private Player winner;

    CheckersGame(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currPlayer = 1;
        this.board = new CheckersBoard();
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
        Player player = (currPlayer == 1) ? player1 : player2;
        if (player.equals(step.player)) {
            if (board.makeTurn(step.x1 - 1, step.y1 - 1, step.x2 - 1, step.y2 - 1, currPlayer)) {
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
    public Byte[] drawPicture(@NotNull Player player) {
        return new Byte[0];
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
}
