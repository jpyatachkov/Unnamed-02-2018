package ru.shipcollision.api.mechanics.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.shipcollision.api.mechanics.GameMechanics;
import ru.shipcollision.api.websockets.MessageHandler;
import ru.shipcollision.api.websockets.MessageHandlerContainer;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@SuppressWarnings("WeakerAccess")
@Component
public class MakeMoveHandler extends MessageHandler<MakeMove> {

    public static final Logger LOGGER = LoggerFactory.getLogger(MakeMove.class);

    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    @NotNull
    private final GameMechanics gameMechanics;

    public MakeMoveHandler(@NotNull MessageHandlerContainer messageHandlerContainer,
                           @NotNull GameMechanics gameMechanics) {
        super(MakeMove.class);
        this.messageHandlerContainer = messageHandlerContainer;
        this.gameMechanics = gameMechanics;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(MakeMove.class, this);
    }

    @Override
    public void handle(@NotNull MakeMove message, @NotNull Long forUser) {
        LOGGER.info("Сделан ход, сообщение от клиента");
        gameMechanics.makeMove(message.coords, forUser);
    }
}
