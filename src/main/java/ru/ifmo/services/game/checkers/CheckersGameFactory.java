package ru.ifmo.services.game.checkers;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.GameFactory;

public class CheckersGameFactory implements GameFactory<CheckersStep, CheckersGame<CheckersStep>> {
    @Override
    public int maxNumberPlayers() {
        return 2;
    }

    @Override
    public int minNumberPlayers() {
        return 2;
    }

    @NotNull
    @Override
    public CheckersGame<CheckersStep> getGame(@NotNull Player... player) {
        assert player.length == 2;
        return new CheckersGame<>(player[0], player[1]);
    }

    @NotNull
    @Override
    public String getInfo() {
        return "checkers";
    }

    @NotNull
    @Override
    public CheckersGame<CheckersStep> fromJson(@NotNull String json) {
        return null;
    }
}
