package ru.shipcollision.api.mechanics.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.shipcollision.api.mechanics.base.Cell;
import ru.shipcollision.api.websockets.MessageHandler;
import ru.shipcollision.api.websockets.MessageHandlerContainer;
import ru.shipcollision.api.websockets.RemotePointService;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Component
public class MakeMooveHandler extends MessageHandler<MakeMoove> {

    public static final Logger LOGGER = LoggerFactory.getLogger(MakeMoove.class);
    @NotNull
    public final RemotePointService remotePointService;

    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public MakeMooveHandler(@NotNull MessageHandlerContainer messageHandlerContainer, @NotNull RemotePointService remotePointService) {
        super(MakeMoove.class);
        this.messageHandlerContainer = messageHandlerContainer;
        this.remotePointService = remotePointService;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(MakeMoove.class, this);
    }

    @Override
    public void handle(@NotNull MakeMoove message, @NotNull Long forUser) {
        LOGGER.info("Сделан ход, сообщение от клиента");
        try {
            remotePointService.sendMessageToUser(forUser, new MoveDone(true, "All ok", message.coords, Cell.DESTROYED));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
