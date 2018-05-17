package ru.shipcollision.api.mechanics.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.shipcollision.api.websockets.MessageHandler;
import ru.shipcollision.api.websockets.MessageHandlerContainer;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
public class MakeMooveHandler extends MessageHandler<MakeMoove> {

    public static final Logger LOGGER = LoggerFactory.getLogger(MakeMoove.class);
    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public MakeMooveHandler(@NotNull MessageHandlerContainer messageHandlerContainer) {
        super(MakeMoove.class);
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(MakeMoove.class, this);
    }

    @Override
    public void handle(@NotNull MakeMoove message, @NotNull Long forUser) {
        LOGGER.info("Сделан ход, сообщение от клиента");
    }
}
