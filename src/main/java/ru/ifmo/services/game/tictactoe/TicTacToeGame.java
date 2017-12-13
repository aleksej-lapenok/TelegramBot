package ru.ifmo.services.game.tictactoe;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Game;
import ru.ifmo.telegram.bot.services.game.Step;

/**
 * Created by Cawa on 02.12.2017.
 */
public class TicTacToeGame<S extends TTTStep> implements Game<S>{
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

    TicTacToeGame(Player player1, Player player2, boolean startWithFirst) {
        p1 = player1;
        p2 = player2;
        currPlayer = startWithFirst ? 1 : -1;
        board = new Board();
    }

    private boolean checkWinner() {
        return board.hasThreeInARow();
    }

    @Override
    @NotNull
    public String step(@NotNull S step) {
        if (currPlayer == 0) {
            return "Game is won by " + winner.getName();
        }
        Player p = (currPlayer == 1) ? p1 : p2;
        if (p.equals(step.p)) {
            if (board.makeTurn(step.x, step.y, currPlayer)) {
                if (checkWinner()) {
                    winner = p;
                    return winner.getName() + " won";
                } else {
                    currPlayer = currPlayer * (-1);
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
    public String getMessage(@NotNull Player p) {
        if (currPlayer == 0) {
            return p.getName() + " won";
        }
        if (p.equals(p1)) {
            if (currPlayer == 1) {
                return "Make your turn";
            }
        }
        if (p.equals(p2))  {
// todo:
        }
        return null;
    }

    public String getInfo(Player p) {
        if (p.equals(p1) || p.equals(p2)) {
            StringBuilder sb = new StringBuilder();
            if (currPlayer != 0) {
                sb.append("Current player: ");
                sb.append(currPlayer == 1 ? p1.getName(): p2.getName());
            } else {
                sb.append("Winner: ");
                sb.append(winner.getName());
            }
            sb.append('\n');
            sb.append(board.toString());
            return sb.toString();
        } else {
            return "Wrong player";
        }
    }

    @Override
    public void finish(@NotNull Player player) {
        //todo: write smth
    }

    @NotNull
    @Override
    public String toJson() {
        // just part of db system, I will make issue for it
        return "";
        // todo: write this method
    }
}


