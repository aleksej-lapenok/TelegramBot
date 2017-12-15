package ru.ifmo.services.game;

import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard;

import java.io.File;

public class GameUpdate {
    private final File picture;
    private final String text;
    private final Keyboard keyboard;

    public GameUpdate(String message, Keyboard keyboard, File file) {
        this.text = message;
        this.keyboard = keyboard;
        this.picture = file;
    }

    GameUpdate(String message, File file) {
        this(message, null, file);
    }

    public String getText() {
        return text;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public File getPicture() {
        return picture;
    }
}
