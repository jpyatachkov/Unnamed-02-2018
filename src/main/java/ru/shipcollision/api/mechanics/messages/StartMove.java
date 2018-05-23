package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.websockets.Message;

public class StartMove extends Message {
    public String message;

    public StartMove(String message) {
        this.message = message;
    }
}
