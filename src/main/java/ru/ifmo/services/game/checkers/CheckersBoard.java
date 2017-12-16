package ru.ifmo.services.game.checkers;

import ru.ifmo.services.game.checkers.CheckersUtils.*;
import ru.ifmo.telegram.bot.services.telegramApi.classes.Button;
import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CheckersBoard {
    public int whiteCount = 12;
    public int blackCount = 12;
    private final static int SIZE = 8;
    //(0,0) - top left
    private List<List<CheckersTile>> tiles;

    Keyboard getKeyboard(boolean reversed) {
        Keyboard keyboard = new Keyboard();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (reversed) {
                    String data = "/turn " + (SIZE - i) + " " + (SIZE - j);
                    keyboard.addButton(new Button("callback_data", data, tiles.get(SIZE - 1 - i).get(SIZE - 1 - j).toString()));
                } else {
                    String data = "/turn " + (i + 1) + " " + (j + 1);
                    keyboard.addButton(new Button("callback_data", data, tiles.get(i).get(j).toString()));
                }
            }
            keyboard.addRow();
        }
        return keyboard;
    }

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
                        if (x2 == SIZE - 1) {
                            checker = Checker.WHITE_QUEEN;
                        }
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
                        if (x2 == 0) {
                            checker = Checker.BLACK_QUEEN;
                        }
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
                if (Math.abs(x2 - x1) != Math.abs(y2 - y1)) {
                    return false;
                }
                int up = x1 < x2 ? 1 : -1;
                int right = y2 > y1 ? 1 : -1;
                for (int i = x1, j = y1; i != x2; i += up, j += right) {
                    if (!tiles.get(i).get(j).isFree()
                            && tiles.get(i + up).get(j + right).isFree()) {
                        if (tiles.get(i).get(j).getChecker() == Checker.BLACK_SIMPLE
                                || tiles.get(i).get(j).getChecker() == Checker.BLACK_QUEEN) {
                            tiles.get(i).get(j).setChecker(Checker.NONE);
                            blackCount--;
                        }
                    }
                }
                break;
            case BLACK_QUEEN:
                if (Math.abs(x1 - x2) != Math.abs(y2 - y1)) {
                    return false;
                }
                int down = x1 > x2 ? 1 : -1;
                int left = y2 < y1 ? 1 : -1;
                for (int i = x1, j = y1; i != x2; i -= down, j -= left) {
                    if (!tiles.get(i).get(j).isFree()
                            && tiles.get(i - down).get(j - left).isFree()) {
                        if (tiles.get(i).get(j).getChecker() == Checker.WHITE_SIMPLE
                                || tiles.get(i).get(j).getChecker() == Checker.WHITE_QUEEN) {
                            tiles.get(i).get(j).setChecker(Checker.NONE);
                            whiteCount--;
                        }
                    }
                }
                break;
        }
        tiles.get(x1).get(y1).setChecker(Checker.NONE);
        tiles.get(x2).get(y2).setChecker(checker);
        return true;
    }

    void clear() {
        tiles.forEach(it -> it.forEach(CheckersTile::clear));
    }

    CheckersBoard() {
        tiles = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            tiles.add(Arrays.stream(new CheckersTile[SIZE]).map(it -> new CheckersTile()).collect(Collectors.toList()));
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (i < 3 && i % 2 != j % 2) {
                    tiles.get(i).get(j).setChecker(Checker.WHITE_SIMPLE);
                }
                if (i > 4 && i % 2 != j % 2) {
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

    public String toReverseString() {
        StringBuilder sb = new StringBuilder();
        for (int i = SIZE - 1; i >= 0; i--) {
            for (int j = SIZE - 1; j >= 0; j--) {
                sb.append(tiles.get(i).get(j).toString());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
