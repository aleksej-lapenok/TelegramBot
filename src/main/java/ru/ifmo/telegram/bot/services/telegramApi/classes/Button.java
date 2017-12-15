package ru.ifmo.telegram.bot.services.telegramApi.classes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Button {
    String type;
    String data;
    String name;

    public Button(String t, String d) {
        type = t;
        data = d;
        name = "0";
    }

    public Button(String t, String d, String n) {
        type = t;
        data = d;
        name = n;
    }

    @Override
    public String toString() {
        return "{text: *, " + type + ": " + data + "}";
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("text", name);
        object.addProperty(type, data);
        return object;
    }

}
