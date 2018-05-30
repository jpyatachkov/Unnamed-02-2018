package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.mechanics.base.Coordinates;
import ru.shipcollision.api.websockets.Message;

import javax.validation.constraints.NotNull;

/**
 * Запрос на ход.
 */
@SuppressWarnings("PublicField")
public class RequestGamePermission extends Message {

    public @NotNull Coordinates coords;
}
