package ru.ifmo.services.game.tictactoe;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Step;

/**
 * Created by Cawa on 02.12.2017.
 */
public class TTTStep implements Step {
    Player player;
    int x, y;

//    TTTStep(Player player, int x, int y) {
//        this.player = player;
//        this.x = x;
//        this.y = y;
//    }

    TTTStep(Player player, String data) {
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

    @NotNull
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "Player " +
                player.getName() +
                "\nMakes turn on " +
                Integer.toString(x) +
                ' ' +
                Integer.toString(y);
    }
}

