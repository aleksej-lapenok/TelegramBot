package ru.ifmo.services.game.tictactoe;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.GameFactory;

@Service
public class TTTGameFactory implements GameFactory<TTTStep, TicTacToeGame<TTTStep>> {
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
    public TicTacToeGame<TTTStep> getGame(@NotNull Player... player) {
        assert player.length == 2;
        return new TicTacToeGame<>(player[0], player[1]);
    }

    @NotNull
    @Override
    public String getInfo() {
        return "test game";
    }

    @NotNull
    @Override
    public TicTacToeGame<TTTStep> fromJson(@NotNull String json) {
        return null;
        //todo: write this method
    }
}
