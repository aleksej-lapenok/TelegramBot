package ru.ifmo.services.game.pairs;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.GameFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PairsGameFactory implements GameFactory<PairsStep, PairsGame<PairsStep>> {
    @Override
    public int maxNumberPlayers() {
        return 1;
    }

    @Override
    public int minNumberPlayers() {
        return 1;
    }

    @NotNull
    @Override
    public PairsGame<PairsStep> getGame(@NotNull Player... player) {
        assert player.length == 1;
        return new PairsGame<>(player[0]);
    }

    @NotNull
    @Override
    public String getInfo() {
        return "pairs";
    }

    @NotNull
    @Override
    public PairsGame<PairsStep> fromJson(@NotNull String json, @NotNull Player... players) {
        throw new NotImplementedException();
    }
}
