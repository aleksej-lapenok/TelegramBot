package ru.ifmo.services.game.checkers;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.StepFactory;

public class CheckersStepFactory implements StepFactory<CheckersStep> {
    @NotNull
    @Override
    public CheckersStep getStep(@NotNull String str, @NotNull Player p) {
        return new CheckersStep(p, str);
    }
}
