package ru.ifmo.services.game.pairs;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Step;

public class PairsStep implements Step {
    Player player;
    int x, y;
    @NotNull
    @Override
    public Player getPlayer() {
        return player;
    }

    PairsStep(Player player, String data) {
        String[] s = data.split(" ");
        if (s.length == 2) {
            x = s[0].charAt(0) - '0';
            y = s[1].charAt(0) - '0';
        } else {
            x = 0;
            y = 0;
        }
        this.player = player;
    }

    @Override
    public String toString() {
        return "Player " +
                player.getName() +
                "\nChoose pictures " +
                Integer.toString(x) +
                ' ' +
                Integer.toString(y);
    }
}
