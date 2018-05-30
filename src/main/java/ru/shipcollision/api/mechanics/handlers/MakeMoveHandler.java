package ru.shipcollision.api.mechanics.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.shipcollision.api.mechanics.GameMechanics;
import ru.shipcollision.api.mechanics.messages.RequestGamePermission;
import ru.shipcollision.api.websockets.MessageHandler;
import ru.shipcollision.api.websockets.MessageHandlerContainer;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
public class MakeMoveHandler extends MessageHandler<RequestGamePermission> {

    public static final Logger LOGGER = LoggerFactory.getLogger(RequestGamePermission.class);

    private final @NotNull MessageHandlerContainer messageHandlerContainer;

    private final @NotNull GameMechanics gameMechanics;

    public MakeMoveHandler(@NotNull MessageHandlerContainer messageHandlerContainer,
                           @NotNull GameMechanics gameMechanics) {
        super(RequestGamePermission.class);
        this.messageHandlerContainer = messageHandlerContainer;
        this.gameMechanics = gameMechanics;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(RequestGamePermission.class, this);
    }

    @Override
    public void handle(@NotNull RequestGamePermission message, @NotNull Long forUser) {
        final String loggerMessage = String.format(
                "Ход клиента %d, координаты [%d, %d]",
                forUser,
                message.coords.getI(),
                message.coords.getJ()
        );
        LOGGER.info(loggerMessage);

        gameMechanics.makeMove(message.coords, forUser);
    }
}
