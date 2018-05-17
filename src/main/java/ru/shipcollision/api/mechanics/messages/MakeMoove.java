package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.mechanics.base.Coordinates;
import ru.shipcollision.api.websockets.Message;

import javax.validation.constraints.NotNull;

public class MakeMoove extends Message {

    @NotNull
    public Coordinates coords;
}
