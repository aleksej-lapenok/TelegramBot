package ru.ifmo.telegram.bot.services.telegramApi;

public class TgException extends Exception {

    public TgException() {
        super();
    }

    public TgException(String msg) {
        super(msg);
    }

    public TgException(String message, Throwable cause) {
        super(message, cause);
    }

    public TgException(Throwable cause) {
        super(cause);
    }
}
