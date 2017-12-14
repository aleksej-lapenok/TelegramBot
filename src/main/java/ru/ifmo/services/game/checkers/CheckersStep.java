package ru.ifmo.services.game.checkers;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Step;

public class CheckersStep implements Step {
    Player player;
    int x1, y1, x2, y2;

    CheckersStep(Player player, int x1, int y1, int x2, int y2) {
        this.player = player;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    CheckersStep(Player player, String data) {
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
    @NotNull
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "Player " +
                player.getName() +
                "\nMakes turn from " +
                Integer.toString(x1) +
                ' ' +
                Integer.toString(y1) +
                " to " +
                Integer.toString(x2) +
                ' ' +
                Integer.toString(y2);
    }
}
