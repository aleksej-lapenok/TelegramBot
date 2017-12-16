package ru.ifmo.services.game;

public class GameException extends Exception {

    public GameException() {
        super();
    }

    public GameException(String msg) {
        super(msg);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameException(Throwable cause) {
        super(cause);
    }
}
