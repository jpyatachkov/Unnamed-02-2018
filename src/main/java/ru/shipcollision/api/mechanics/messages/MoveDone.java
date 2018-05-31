package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.mechanics.base.CellStatus;
import ru.shipcollision.api.mechanics.base.Coordinates;
import ru.shipcollision.api.websockets.Message;

import javax.validation.constraints.NotNull;

/**
 * Результат хода.
 */
@SuppressWarnings("PublicField")
public class MoveDone extends Message {

    public @NotNull String message;

    public Coordinates coord;

    public CellStatus cell;

    public int score;

    public MoveDone(String message, Coordinates coord, CellStatus cell, int score) {
        this.message = message;
        this.coord = coord;
        this.cell = cell;
        this.score = score;
    }
}
