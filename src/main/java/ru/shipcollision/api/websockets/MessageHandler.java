package ru.shipcollision.api.websockets;

import ru.shipcollision.api.exceptions.ApiException;

import javax.validation.constraints.NotNull;

public abstract class MessageHandler<T extends Message> {

    private final @NotNull Class<T> clazz;

    public MessageHandler(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    public void handleMessage(@NotNull Message message, @NotNull Long id) {
        try {
            handle(clazz.cast(message), id);
        } catch (ClassCastException ex) {
            throw new ApiException(ex);
        }
    }

    public abstract void handle(@NotNull T message, @NotNull Long id);
}