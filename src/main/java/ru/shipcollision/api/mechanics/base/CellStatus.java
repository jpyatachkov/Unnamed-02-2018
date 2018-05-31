package ru.shipcollision.api.mechanics.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

/**
 * Статус ячейки на поле игрока.
 */
public enum CellStatus {
    // Пустая клетка.
    EMPTY,
    // Корабль.
    BUSY,
    // Попали в клетку игрока.
    DESTROYED,
    // Игрок попал в клетку другого игрока.
    DESTROYED_OTHER,
    // Промах.
    MISSED
}
