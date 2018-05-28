package ru.shipcollision.api.mechanics.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.shipcollision.api.dao.UserDAO;
import ru.shipcollision.api.mechanics.GameMechanics;
import ru.shipcollision.api.mechanics.models.GamePlayer;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.websockets.MessageHandler;
import ru.shipcollision.api.websockets.MessageHandlerContainer;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
public class JoinGameHandler extends MessageHandler<JoinGame> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JoinGameHandler.class);
    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;
    @NotNull
    private final UserDAO userDAO;
    @NotNull
    private final GameMechanics gameMechanics;

    public JoinGameHandler(@NotNull MessageHandlerContainer messageHandlerContainer,
                           @NotNull UserDAO userDAO,
                           @NotNull GameMechanics gameMechanics) {
        super(JoinGame.class);
        this.messageHandlerContainer = messageHandlerContainer;
        this.userDAO = userDAO;
        this.gameMechanics = gameMechanics;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(JoinGame.class, this);
    }

    @Override
    public void handle(@NotNull JoinGame message, @NotNull Long forUser) {
        LOGGER.info("Присоединение к игре");
        User user = userDAO.findById(forUser);
        GamePlayer player = new GamePlayer(user, message, gameMechanics.getShipsCount(message.count.intValue()));
        gameMechanics.addWaiter(player);
    }
}
