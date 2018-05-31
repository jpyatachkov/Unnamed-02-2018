package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.websockets.Message;

@SuppressWarnings("PublicField")
public class ErrorMessage extends Message {

    public String description;

    public ErrorMessage(String description) {
        this.description = description;
    }
}
