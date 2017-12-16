package ru.ifmo.services.game.pairs;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.StepFactory;

public class PairsStepFactory implements StepFactory<PairsStep> {
    @NotNull
    @Override
    public PairsStep getStep(@NotNull String str, @NotNull Player p) {
        return new PairsStep(p, str);
    }
}
