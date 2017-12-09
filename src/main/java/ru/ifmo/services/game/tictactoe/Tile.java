package ru.ifmo.services.game.tictactoe;

import org.jetbrains.annotations.Contract;

/**
 * Created by Cawa on 02.12.2017.
 */
public class Tile {
    private Integer state;

    Tile() {
        state = 0;
    }

    @Contract(pure = true)
    private boolean isFree() {
        return 0 == state;
    }

    void clear() {
        state = 0;
    }

    boolean makeTurn(int i) {
        if (!isFree()) {
            return false;
        }
        if (i > 0) {
            state = 1;
        } else {
            state = -1;
        }
        return true;
    }

    @Override
    public String toString() {
        switch (state) {
            case -1:
                return "0";
            case 1:
                return "x";
        }
        return "*";
    }

    boolean equals(Tile obj) {
        return state.equals(obj.state);
    }
}
