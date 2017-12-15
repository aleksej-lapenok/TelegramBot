package ru.ifmo.services.game.tictactoe;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;

/**
 * Created by Cawa on 02.12.2017.
 */
public class Tile {

    private TileState state;

    Tile() {
        state = TileState.EMPTY;
    }

    Tile(JsonObject jsonObject) {
        state = TileState.valueOf(jsonObject.get("state").getAsString());
    }

    @Contract(pure = true)
    boolean isFree() {
        return state == TileState.EMPTY;
    }

    void clear() {
        state = TileState.EMPTY;
    }

    boolean makeTurn(TileState newState) {
        if (!isFree()) {
            return false;
        }
        state = newState;
        return true;
    }

    @Override
    public String toString() {
        switch (state) {
            case ZERO:
                return "0";
            case MARK:
                return "x";
            case EMPTY:
                return "*";
            default:
                return "_";
        }
    }

    boolean equals(Tile obj) {
        return state.equals(obj.state);
    }

    enum TileState {
        EMPTY,
        ZERO,
        MARK
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("state", state.toString());
        return object;
    }

}

