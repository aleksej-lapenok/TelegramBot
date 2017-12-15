package ru.ifmo.services.game;

import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard;

import java.io.File;

public class GameUpdate {
    File picture;
    String text;
    Keyboard keyboard = null;

    public GameUpdate(String message, Keyboard keyboard, File file) {
        this.text = message;
        this.keyboard = keyboard;
        this.picture = file;
    }

    GameUpdate(String message, File file) {
        this.text = message;
        this.picture = file;
    }

    public String getText() {
        return text;
    }
}
