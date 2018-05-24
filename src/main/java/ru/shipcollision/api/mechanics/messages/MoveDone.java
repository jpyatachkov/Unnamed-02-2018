package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.mechanics.base.Cell;
import ru.shipcollision.api.mechanics.base.Coordinates;
import ru.shipcollision.api.websockets.Message;

import javax.validation.constraints.NotNull;

@SuppressWarnings("WeakerAccess")
public class MoveDone extends Message {

    @NotNull
    public String message;

    public Coordinates coord;

    public Cell cell;

    public int score;

    public MoveDone(String message, Coordinates coord, Cell cell, int score) {
        this.message = message;
        this.coord = coord;
        this.cell = cell;
        this.score = score;
    }
}
