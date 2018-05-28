package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.mechanics.base.Cell;
import ru.shipcollision.api.websockets.Message;

import javax.validation.constraints.NotNull;
import java.util.List;

public class JoinGame extends Message {

    @NotNull
    public List<List<Cell>> field;
    @NotNull
    public Long count;
}
