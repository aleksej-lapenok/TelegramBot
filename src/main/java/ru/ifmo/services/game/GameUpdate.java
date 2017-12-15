package ru.ifmo.services.game;

import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard;

import java.io.File;

public class GameUpdate {
    File picture;
    String text;
    Keyboard keyboard = null;

    public GameUpdate(String msg, Keyboard kbd, File file) {
        text = msg;
        keyboard = kbd;
        picture = file;
    }

    GameUpdate(String msg, File file) {
        text = msg;
        picture = file;
    }
}
