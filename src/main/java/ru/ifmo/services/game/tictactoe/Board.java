package ru.ifmo.services.game.tictactoe;

import ru.ifmo.telegram.bot.services.telegramApi.TypeUpdate;
import ru.ifmo.telegram.bot.services.telegramApi.Update;
import ru.ifmo.telegram.bot.services.telegramApi.classes.Button;
import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Cawa on 02.12.2017.
 */
public class Board {
    //(1,1) - top left
    private List<List<Tile>> tiles;

    boolean makeTurn(int x, int y, int sign) {
        return (x >= 1) && (x <= 3) && (y >= 1) && (y <= 3) &&
                tiles.get(y - 1).get(x - 1).makeTurn(sign);
    }

    void clear() {
        tiles.forEach(it -> it.forEach(Tile::clear));
    }

    Keyboard getKeyboard() {
        Keyboard keyboard = new Keyboard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String data = "/skip";
                if (tiles.get(i).get(j).isFree()) {
                    data = "/turn " + (j + 1) + " " + (i + 1);
                }
                keyboard.addButton(new Button("callback_data", data, tiles.get(i).get(j).toString()));
            }
            keyboard.addRow();
        }
        return keyboard;
    }

    boolean hasThreeInARow() {
        // lines
        for (int i = 0; i < 3; i++) {
            Tile t = tiles.get(0).get(i);
            boolean found = !t.toString().equals("*");
            for (int j = 1; j < 3; j++) {
                found &= t.equals(tiles.get(j).get(i));
            }
            if (found) {
                return true;
            }
        }
        // rows
        for (int i = 0; i < 3; i++) {
            Tile t = tiles.get(i).get(0);
            boolean found = !t.toString().equals("*");
            for (int j = 1; j < 3; j++) {
                found &= t.equals(tiles.get(i).get(j));
            }
            if (found) {
                return true;
            }
        }
        // diag
        Tile t = tiles.get(1).get(1);
        return t.equals(tiles.get(0).get(0)) && t.equals(tiles.get(2).get(2)) && !t.toString().equals("*") ||
                t.equals(tiles.get(0).get(2)) && t.equals(tiles.get(2).get(0)) && !t.toString().equals("*");
    }

    Board() {
        tiles = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            tiles.add(Arrays.stream(new Tile[3]).map(it -> new Tile()).collect(Collectors.toList()));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (List<Tile> list:tiles) {
            for (Tile tile : list) {
                sb.append(tile.toString());
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    boolean isFull() {
        for (List<Tile> line : tiles) {
            for (Tile tile : line) {
                if (tile.toString().equals("*")) {
                    return false;
                }
            }
        }
        return true;
    }
}
