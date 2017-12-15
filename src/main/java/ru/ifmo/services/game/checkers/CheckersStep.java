package ru.ifmo.services.game.checkers;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Step;

public class CheckersStep implements Step {
    Player player;
    int x, y;

    CheckersStep(Player player, String data) {
        String[] s = data.split(" ");
        if (s.length == 4) {
            x = s[0].charAt(0) - '0';
            y = s[1].charAt(0) - '0';
        } else {
            x = 1;
            y = 1;
        }
        this.player = player;
    }
    @NotNull
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "Player " +
                player.getName() +
                "\nMakes turn " +
                Integer.toString(x) +
                ' ' +
                Integer.toString(y);
    }
}
