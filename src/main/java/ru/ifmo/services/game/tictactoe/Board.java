package ru.ifmo.services.game.tictactoe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
    private final int SIZE = 3;

    boolean makeTurn(int row, int line, Tile.TileState state) {
        return (row > 0) && (row <= SIZE) && (line > 0) && (line <= SIZE) &&
                tiles.get(line - 1).get(row - 1).makeTurn(state);
    }

    void clear() {
        tiles.forEach(it -> it.forEach(Tile::clear));
    }

    Keyboard getKeyboard() {
        Keyboard keyboard = new Keyboard();
        for (int line = 0; line < SIZE; line++) {
            for (int row = 0; row < SIZE; row++) {
                String data = "/skip";
                if (tiles.get(line).get(row).isFree()) {
                    data = "/turn " + (row + 1) + " " + (line + 1);
                }
                keyboard.addButton(new Button("callback_data", data, tiles.get(line).get(row).toString()));
            }
            keyboard.addRow();
        }
        return keyboard;
    }

    boolean hasThreeInARow() {
        // rows
        for (int row = 0; row < SIZE; row++) {
            Tile t = tiles.get(0).get(row);
            if (!t.isFree() && t.equals(tiles.get(1).get(row)) && t.equals(tiles.get(2).get(row))) {
                return true;
            }
        }
        // lines
        for (int line = 0; line < SIZE; line++) {
            Tile t = tiles.get(line).get(0);
            if (!t.isFree() && t.equals(tiles.get(line).get(1)) && t.equals(tiles.get(line).get(2))) {
                return true;
            }
        }
        // diag
        Tile t = tiles.get(1).get(1);
        return !t.isFree() && (
                t.equals(tiles.get(0).get(0)) && t.equals(tiles.get(2).get(2)) ||
                t.equals(tiles.get(0).get(2)) && t.equals(tiles.get(2).get(0)));
    }

    Board() {
        tiles = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            tiles.add(Arrays.stream(new Tile[SIZE]).map(it -> new Tile()).collect(Collectors.toList()));
        }
    }

    Board(JsonObject json) {
        tiles = new ArrayList<>(SIZE);
        for (JsonElement line : json.get("tiles").getAsJsonArray()) {
            ArrayList<Tile> lineOfTiles = new ArrayList<>();
            for (JsonElement t : line.getAsJsonArray()) {
                lineOfTiles.add(new Tile(t.getAsJsonObject()));
            }
            tiles.add(lineOfTiles);
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
                if (tile.isFree()) {
                    return false;
                }
            }
        }
        return true;
    }

    public JsonElement toJson() {
        JsonArray array = new JsonArray();
        for (List<Tile> list : tiles) {
            JsonArray local = new JsonArray();
            for (Tile t : list) {
                local.add(t.toJson());
            }
            array.add(local);
        }
        JsonObject k = new JsonObject();
        k.add("tiles", array);
        return k;
    }
}
