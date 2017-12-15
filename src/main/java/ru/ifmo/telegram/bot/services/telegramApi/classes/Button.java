package ru.ifmo.telegram.bot.services.telegramApi.classes;

import com.google.gson.JsonObject;

public class Button {
    String type;
    String data;
    String name;

    public Button(String type, String data) {
        this.type = type;
        this.data = data;
        name = "0";
    }

    public Button(String type, String data, String name) {
        this.type = type;
        this.data = data;
        this.name = name;
    }

    @Override
    public String toString() {
        return "{text: *, " + type + ": " + data + "}";
    }

    JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("text", name);
        object.addProperty(type, data);
        return object;
    }

}
