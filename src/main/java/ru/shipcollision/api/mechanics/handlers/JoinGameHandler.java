package ru.shipcollision.api.mechanics.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.shipcollision.api.dao.UserDAO;
import ru.shipcollision.api.mechanics.GameMechanics;
import ru.shipcollision.api.mechanics.GameRulesHelper;
import ru.shipcollision.api.mechanics.messages.JoinGame;
import ru.shipcollision.api.mechanics.models.Player;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.websockets.MessageHandler;
import ru.shipcollision.api.websockets.MessageHandlerContainer;
import ru.shipcollision.api.websockets.RemotePointService;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
public class JoinGameHandler extends MessageHandler<JoinGame> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JoinGameHandler.class);

    private final @NotNull MessageHandlerContainer messageHandlerContainer;

    private final @NotNull UserDAO userDAO;

    private final @NotNull GameMechanics gameMechanics;

    private final @NotNull RemotePointService remotePointService;

    public JoinGameHandler(@NotNull MessageHandlerContainer messageHandlerContainer,
                           @NotNull UserDAO userDAO,
                           @NotNull GameMechanics gameMechanics,
                           @NotNull RemotePointService remotePointService) {
        super(JoinGame.class);
        this.messageHandlerContainer = messageHandlerContainer;
        this.userDAO = userDAO;
        this.gameMechanics = gameMechanics;
        this.remotePointService = remotePointService;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(JoinGame.class, this);
    }

    @Override
    public void handle(@NotNull JoinGame message, @NotNull Long forUser) {
        final String loggerMessage = String.format(
                "Клиент %d желает присоединиться к игре на %d человек",
                forUser,
                message.count
        );
        LOGGER.info(loggerMessage);

        final User user = userDAO.findById(forUser);
        final Player player = new Player(
                user,
                message.field,
                GameRulesHelper.getShipsCountForPlayers(message.count.intValue()),
                message.count
        );

        gameMechanics.addWaiter(player);
    }
}
