package ru.ifmo.telegram.bot.services.telegramApi.classes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Keyboard {
    List<List<Button>> buttons;

    public Keyboard() {
        buttons = new LinkedList<>();
        buttons.add(new LinkedList<>());
    }

    public void addButton(Button b) {
        buttons.get(buttons.size() - 1).add(b);
    }

    public void addRow() {
        buttons.add(new LinkedList<>());
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder();
        JsonArray array = new JsonArray();
        ans.append('[');
        for (List<Button> list : buttons) {
            JsonArray local = new JsonArray();
            ans.append('[');
            for (Button b : list) {
                ans.append(b.toString());
                ans.append(",");
            }
            ans.setCharAt(ans.length() - 1, ']');
            ans.append(',');
        }
        ans.setCharAt(ans.length() - 1, ']');
        return ans.toString();
    }


    public JsonElement toJson() {
        JsonArray array = new JsonArray();
        for (List<Button> list : buttons) {
            JsonArray local = new JsonArray();
            for (Button b : list) {
                local.add(b.toJson());
            }
            array.add(local);
        }
        JsonObject k = new JsonObject();
        k.add("inline_keyboard", array);
        return k;
    }
}

