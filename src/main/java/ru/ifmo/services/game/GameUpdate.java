package ru.ifmo.services.game;

import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard;

import java.io.File;

public class GameUpdate {
    private final byte[] picture;
    private final String text;
    private final Keyboard keyboard;

    public GameUpdate(String message, Keyboard keyboard, byte[] file) {
        this.text = message;
        this.keyboard = keyboard;
        this.picture = file;
    }

    GameUpdate(String message, byte[] file) {
        this(message, new Keyboard(), file);
    }

    public String getText() {
        return text;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public byte[] getPicture() {
        return picture;
    }
}
