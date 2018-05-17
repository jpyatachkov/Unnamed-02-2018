package ru.shipcollision.api.mechanics.base;

public enum Cell {
    //Пустая клетка
    EMPTY,
    //Корабль
    BYSY,
    //Попали в клетку игрока
    DESTROYED,
    //Игрок попал в клетку другого игрока
    DESTROYED_OTHER,
    //Промах
    MISSED
}
