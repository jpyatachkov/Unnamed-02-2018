package ru.shipcollision.api.mechanics.base;

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
