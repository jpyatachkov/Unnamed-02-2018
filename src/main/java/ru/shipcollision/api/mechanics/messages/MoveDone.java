package ru.shipcollision.api.mechanics.messages;

import com.github.javafaker.Bool;
import ru.shipcollision.api.mechanics.base.Cell;
import ru.shipcollision.api.mechanics.base.Coordinates;
import ru.shipcollision.api.websockets.Message;

public class MoveDone extends Message {

    public boolean flag;

    public String message;

    public Coordinates coord;
    public Cell cell;

    public MoveDone(boolean flag, String message, Coordinates coord, Cell cell) {
        this.flag = flag;
        this.message = message;
        this.coord = coord;
        this.cell = cell;
    }
}
