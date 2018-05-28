package ru.shipcollision.api.mechanics.base;

public class Coordinates {

    private int i;

    private int j;

    public Coordinates() {
    }

    public Coordinates(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }
}
