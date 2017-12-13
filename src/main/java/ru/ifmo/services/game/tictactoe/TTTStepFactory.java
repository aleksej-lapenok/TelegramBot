package ru.ifmo.services.game.tictactoe;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.StepFactory;

@Service
public class TTTStepFactory implements StepFactory<TTTStep> {
    @NotNull
    @Override
    public TTTStep getStep(@NotNull String str, @NotNull Player p) {
        return new TTTStep(p, str);
    }
}
