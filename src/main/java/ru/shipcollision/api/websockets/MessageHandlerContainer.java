package ru.shipcollision.api.websockets;

import javax.validation.constraints.NotNull;

public interface MessageHandlerContainer {

    void handle(@NotNull Message message, @NotNull Long id);

    <T extends Message> void registerHandler(@NotNull Class<T> clazz, MessageHandler<T> handler);
}