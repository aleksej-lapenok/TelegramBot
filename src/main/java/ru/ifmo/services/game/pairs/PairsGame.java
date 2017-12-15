package ru.ifmo.services.game.pairs;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.services.game.GameUpdate;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Game;
import ru.ifmo.telegram.bot.services.main.Games;
import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class PairsGame<S extends PairsStep> implements Game<S> {
    private boolean isPlayerSurrender;
    private boolean isFirstCard;
    private int firstCardX;
    private int firstCardY;
    private Player player;
    private PairsBoard board;

    public PairsGame(Player player) {
        this.isPlayerSurrender = false;
        this.player = player;
        this.board = new PairsBoard();
        this.isFirstCard = true;
    }

    @NotNull
    @Override
    public String step(@NotNull S step) {
        if (step.x == 0) {
            board.notSelectAll();
            return "Choose new pair";
        }
        if (!player.equals(step.player)) {
            return "Wrong player tried to make turn";
        }
        if (isPlayerSurrender) {
            return "You're surrender";
        }
        if (isFirstCard) {
            if (board.select(step.x - 1, step.y - 1)) {
                this.firstCardX = step.x;
                this.firstCardY = step.y;
                isFirstCard = false;
                return "Select pair";
            } else {
                return "Wrong select!!!";
            }
        }
        isFirstCard = true;
        if (board.makeTurn(firstCardX - 1, firstCardY - 1, step.x - 1, step.y - 1)) {
            if (isFinished()) {
                return player.getName() + " won";
            } else {
                return "Turn was made";
            }
        } else {
            return "Wrong turn";
        }
    }

    @NotNull
    @Override
    public File drawPicture(@NotNull Player player) {
        return new File("");
    }

    @NotNull
    @Override
    public String getMessage(@NotNull Player player) {
        if (player.equals(this.player)) {
            StringBuilder sb = new StringBuilder();
            if (!board.isFull()) {
                sb.append("Current player: ");
                sb.append(player.getName());
            } else {
                sb.append("Winner: ");
                sb.append(player.getName());
            }
            return sb.toString();
        } else {
            return "Wrong player";
        }
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
    public void surrender(@NotNull Player player) {
        this.isPlayerSurrender = true;
    }

    @NotNull
    @Override
    public String toJson() {
        return "";
    }

    @NotNull
    @Override
    public List<Player> getPlayes() {
        return Arrays.asList(player);
    }

    @NotNull
    @Override
    public Games getGameId() {
        return Games.PAIRS;
    }

    @Override
    public boolean isFinished() {
        return isPlayerSurrender || board.isFull();
    }

    @Override
    public boolean isCurrent(@NotNull Player player) {
        return this.player.equals(player);
    }
}
