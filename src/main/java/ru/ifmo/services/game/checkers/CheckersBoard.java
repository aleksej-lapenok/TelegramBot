package ru.ifmo.services.game.checkers;

import ru.ifmo.services.game.checkers.CheckersUtils.*;

import java.util.ArrayList;
import java.util.List;

public class CheckersBoard {
    public int whiteCount = 12;
    public int blackCount = 12;
    private final static int SIZE = 8;
    //(0,0) - top left
    private List<List<CheckersTile>> tiles;

    boolean makeTurn(int x1, int y1, int x2, int y2, int player) {
        if ((x1 < 0) || (x1 >= SIZE) || (y1 < 0) || (y1 >= SIZE)
                || (x2 < 0) || (x2 >= SIZE) || (y2 < 0) || (y2 >= SIZE)) {
            return false;
        }
        Checker checker = tiles.get(x1).get(y1).getChecker();
        if (player == 1 && checker != Checker.WHITE_SIMPLE && checker != Checker.WHITE_QUEEN
                || player == -1 && checker != Checker.BLACK_SIMPLE && checker != Checker.BLACK_QUEEN
                || !tiles.get(x2).get(y2).isFree()) {
            return false;
        }
        switch (checker) {
            case WHITE_SIMPLE:
                if (Math.abs(x1 - x2) == 2 && Math.abs(y2 - y1) == 2) {
                    int x = (x1 + x2) / 2;
                    int y = (y2 + y1) / 2;
                    if (tiles.get(x).get(y).getChecker() == Checker.BLACK_SIMPLE
                            || tiles.get(x).get(y).getChecker() == Checker.BLACK_QUEEN) {
                        tiles.get(x).get(y).setChecker(Checker.NONE);
                        blackCount--;
                        break;
                    } else {
                        return false;
                    }
                }
                if (x1 + 1 != x2 || Math.abs(y2 - y1) != 1) {
                    return false;
                }
                if (x2 == SIZE - 1) {
                    checker = Checker.WHITE_QUEEN;
                }
                break;
            case BLACK_SIMPLE:
                if (Math.abs(x1 - x2) == 2 && Math.abs(y2 - y1) == 2) {
                    int x = (x1 + x2) / 2;
                    int y = (y2 + y1) / 2;
                    if (tiles.get(x).get(y).getChecker() == Checker.WHITE_SIMPLE
                            || tiles.get(x).get(y).getChecker() == Checker.WHITE_QUEEN) {
                        tiles.get(x).get(y).setChecker(Checker.NONE);
                        whiteCount--;
                        break;
                    } else {
                        return false;
                    }
                }
                if (x1 - 1 != x2 || Math.abs(y2 - y1) != 1) {
                    return false;
                }
                if (x2 == 0) {
                    checker = Checker.BLACK_QUEEN;
                }
                break;
            case WHITE_QUEEN:
                if (x2 - x1 != Math.abs(y2 - y1)) {
                    return false;
                }
                break;
            case BLACK_QUEEN:
                if (x1 - x2 != Math.abs(y2 - y1)) {
                    return false;
                }
                break;
        }
        tiles.get(x1).get(y1).setChecker(Checker.NONE);
        tiles.get(x2).get(y2).setChecker(checker);
        return true;
    }

    void clear() {
        for (List<CheckersTile> list : tiles) {
            for (CheckersTile tile : list) {
                tile.clear();
            }
        }
    }

    CheckersBoard() {
        tiles = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            tiles.add(new ArrayList<>(SIZE));
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (i < 3 && i % 2 == j % 2) {
                    tiles.get(i).get(j).setChecker(Checker.WHITE_SIMPLE);
                }
                if (i > 4 && i % 2 == j % 2) {
                    tiles.get(i).get(j).setChecker(Checker.BLACK_SIMPLE);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (List<CheckersTile> list : tiles) {
            for (CheckersTile tile : list) {
                sb.append(tile.toString());
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
