package ru.ifmo.services.game.tictactoe;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Game;
import ru.ifmo.telegram.bot.services.main.Games;
import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Cawa on 02.12.2017.
 */
public class TicTacToeGame<S extends TTTStep> implements Game<S> {
    private Player p1, p2;
    private Board board;
    private int currPlayer;
    private Player winner;

    TicTacToeGame(Player player1, Player player2) {
        p1 = player1;
        p2 = player2;
        currPlayer = 1;
        board = new Board();
    }

//    TicTacToeGame(Player player1, Player player2, boolean startWithFirst) {
//        p1 = player1;
//        p2 = player2;
//        currPlayer = startWithFirst ? 1 : -1;
//        board = new Board();
//    }

    private boolean checkWinner() {
        return board.hasThreeInARow();
    }

    @Override
    @NotNull
    public String step(@NotNull S step) {
        if (currPlayer == 0) {
            if (winner != null) {
                return "Game is won by " + winner.getName();
            } else {
                return "Draw";
            }
        }
        Player player = (currPlayer == 1) ? p1 : p2;
        if (player.equals(step.player)) {
            if (board.makeTurn(step.x, step.y, currPlayer)) {
                if (checkWinner()) {
                    winner = player;
                    currPlayer = 0;
                    return winner.getName() + " won";
                } else if (board.isFull()) {
                    currPlayer = 0;
                    winner = null;
                    return "Draw";
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
    public Keyboard getKeyboard() {
        return board.getKeyboard();
    }

    @NotNull
    @Override
    public Byte[] drawPicture(@NotNull Player player) {
        return new Byte[0];
    }

    @NotNull
    @Override
    public String getMessage(@NotNull Player player) {
        return getInfo(player);
    }

    private String getInfo(Player player) {
        if (player.equals(p1) || player.equals(p2)) {
            StringBuilder sb = new StringBuilder();
            if (currPlayer != 0) {
                sb.append("Current player: ");
                sb.append(currPlayer == 1 ? p1.getName() : p2.getName());
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

    @Override
    public void surrender(@NotNull Player player) {
        if (player.equals(p1)) {
            winner = p2;
            currPlayer = 0;
        } else if (player.equals(p2)) {
            winner = p1;
            currPlayer = 0;
        }
    }

    @NotNull
    @Override
    public String toJson() {
        // just part of db system, I will make issue for it
        return "";
        // todo: write this method
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
        return currPlayer == 0 || board.isFull();
    }

    public boolean isCurrent(Player p) {
        return (p1.equals(p) && currPlayer > 0) || (p2.equals(p) && currPlayer < 0);
    }
}


