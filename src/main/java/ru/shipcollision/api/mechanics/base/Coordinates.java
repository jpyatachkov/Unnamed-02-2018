package ru.shipcollision.api.mechanics.base;

/**
 * Координаты выстрела:
 * i - номер строки на поле,
 * j - номер столбца.
 */
@SuppressWarnings("FieldNamingConvention")
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

    public int getJ() {
        return j;
    }
}
