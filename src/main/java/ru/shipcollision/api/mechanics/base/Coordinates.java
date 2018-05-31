package ru.shipcollision.api.mechanics.base;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Coordinates other = (Coordinates) o;
        return i == other.i &&
                j == other.j;
    }

    @Override
    public int hashCode() {

        return Objects.hash(i, j);
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }
}
