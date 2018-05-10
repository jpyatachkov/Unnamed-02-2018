package ru.shipcollision.api.websockets;

import ru.shipcollision.api.exceptions.ApiException;

import javax.validation.constraints.NotNull;

public abstract class MessageHandler<T extends Message> {
    @NotNull
    private final Class<T> clazz;

    public MessageHandler(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    public void handleMessage(@NotNull Message message, @NotNull Long id) {
        try {
            handle(clazz.cast(message), id);
        } catch (ClassCastException ex) {
            throw new ApiException("Can't read incoming message of type " + message.getClass());
        }
    }

    public abstract void handle(@NotNull T message, @NotNull Long id);
}