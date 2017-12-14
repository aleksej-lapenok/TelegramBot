package ru.ifmo.services.game.pairs;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Step;

public class PairsStep implements Step {
    Player player;
    int x1, y1, x2, y2;
    @NotNull
    @Override
    public Player getPlayer() {
        return player;
    }

    PairsStep(Player player, String data) {
        String[] s = data.split(" ");
        if (s.length == 4) {
            x1 = s[0].charAt(0) - '0';
            y1 = s[1].charAt(0) - '0';
            x2 = s[2].charAt(0) - '0';
            y2 = s[3].charAt(0) - '0';
        } else {
            x1 = 0;
            y1 = 0;
            x2 = 0;
            y2 = 0;
        }
        this.player = player;
    }

    @Override
    public String toString() {
        return "Player " +
                player.getName() +
                "\nChoose pictures " +
                Integer.toString(x1) +
                ' ' +
                Integer.toString(y1) +
                " and " +
                Integer.toString(x2) +
                ' ' +
                Integer.toString(y2);
    }
}
