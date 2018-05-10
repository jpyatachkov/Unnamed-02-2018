package ru.shipcollision.api.mechanics.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.shipcollision.api.websockets.MessageHandler;
import ru.shipcollision.api.websockets.MessageHandlerContainer;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
public class JoinGameHandler extends MessageHandler<JoinGame.Request> {
//    @NotNull
//    private final GameMechanics gameMechanics;

    public static final Logger LOGGER = LoggerFactory.getLogger(JoinGameHandler.class);
    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public JoinGameHandler(@NotNull MessageHandlerContainer messageHandlerContainer) {
        super(JoinGame.Request.class);
//        this.gameMechanics = gameMechanics;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(JoinGame.Request.class, this);
    }

    @Override
    public void handle(@NotNull JoinGame.Request message, @NotNull Long forUser) {
//        gameMechanics.addUser(forUser);
        LOGGER.info("СООБЩЕНИЕ ПРИШЛО УРАААА!");
    }
}
