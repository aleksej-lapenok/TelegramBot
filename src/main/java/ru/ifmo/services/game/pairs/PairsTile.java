package ru.ifmo.services.game.pairs;

public class PairsTile {
    private boolean open, select;
    private char id;

    PairsTile() {
        this.open = false;
        this.select = false;
        this.id = 0;
    }

    public boolean isOpen() {
        return this.open;
    }

    public boolean isSelect() {
        return this.select;
    }

    public void revertSelect() {
        this.select = !this.select;
    }

    public void open() {
        this.open = true;
    }

    public void close() {
        this.open = false;
    }

    public void setId(char id) {
        this.id = id;
    }

    public void clear() {
        this.id = 0;
        this.open = false;
        this.select = false;
    }

    @Override
    public String toString() {
        if (select) {
            return "+";
        }
        return open ? "" + id : (id + "").toUpperCase();
    }

    boolean equals(PairsTile obj) {
        return id == obj.id;
    }
}
