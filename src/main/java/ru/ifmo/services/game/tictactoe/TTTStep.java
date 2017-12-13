package ru.ifmo.services.game.tictactoe;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Step;
import ru.ifmo.telegram.bot.services.game.StepFactory;

/**
 * Created by Cawa on 02.12.2017.
 */
public class TTTStep implements Step {
    Player p;
    int x, y;

    TTTStep(Player p, int x, int y) {
        this.p = p;
        this.x = x;
        this.y = y;
    }

    TTTStep(Player p, String data) {
        String[] s = data.split(" ");
        if (s.length == 2) {
            x = s[0].charAt(0) - '0';
            y = s[1].charAt(0) - '0';
        } else {
            x = 0;
            y = 0;
        }
    }

    @NotNull
    @Override
    public Player getPlayer() {
        return p;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Player ");
        sb.append(p.getName());
        sb.append("\nMakes turn on ");
        sb.append(Integer.toString(x));
        sb.append(' ');
        sb.append(Integer.toString(y));
        return sb.toString();
    }
}

