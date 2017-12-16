package ru.ifmo.services.game.checkers;

import org.jetbrains.annotations.Contract;
import ru.ifmo.services.game.checkers.CheckersUtils.*;

public class CheckersTile {
    private Checker state;

    CheckersTile() {
        state = Checker.NONE;
    }

    @Contract(pure = true)
    public boolean isFree() {
        return Checker.NONE == state;
    }

    void clear() {
        state = Checker.NONE;
    }

    void setChecker(Checker checker) {
        state = checker;
    }

    Checker getChecker() {
        return state;
    }

    @Override
    public String toString() {
        switch (state) {
            case WHITE_SIMPLE:
                return "w";
            case BLACK_SIMPLE:
                return "b";
            case WHITE_QUEEN:
                return "W";
            case BLACK_QUEEN:
                return "B";
            default:
                return "-";
        }
    }

    boolean equals(CheckersTile obj) {
        return state.equals(obj.state);
    }
}
