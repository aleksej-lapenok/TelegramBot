package ru.ifmo.services.game.pairs;

public class PairsTile {
    private boolean open;
    private int id;

    PairsTile() {
        this.open = false;
        this.id = 0;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void open() {
        this.open = true;
    }

    public void close() {
        this.open = false;
    }

    @Override
    public String toString() {
        return "" + ((char) (id + (int) 'a'));
    }

    boolean equals(PairsTile obj) {
        return id == obj.id;
    }
}
