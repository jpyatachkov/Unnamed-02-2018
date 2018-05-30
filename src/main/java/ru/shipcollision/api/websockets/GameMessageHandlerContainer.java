package ru.shipcollision.api.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Service
public class GameMessageHandlerContainer implements MessageHandlerContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameMessageHandlerContainer.class);

    private final Map<Class<?>, MessageHandler<?>> handlerMap = new HashMap<>();

    @Override
    public void handle(@NotNull Message message, @NotNull Long id) {
        final MessageHandler<?> messageHandler = handlerMap.get(message.getClass());
        if (messageHandler == null) {
            LOGGER.info(String.format("No handler for message of %s type", message.getClass().getName()));
            return;
        }
        messageHandler.handleMessage(message, id);
        LOGGER.trace("message handled: type =[" + message.getClass().getName() + ']');
    }

    @Override
    public <T extends Message> void registerHandler(@NotNull Class<T> clazz, MessageHandler<T> handler) {
        handlerMap.put(clazz, handler);
    }
}
