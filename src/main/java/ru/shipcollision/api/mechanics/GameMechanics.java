package ru.shipcollision.api.mechanics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.shipcollision.api.dao.UserDAO;
import ru.shipcollision.api.mechanics.models.GamePlayer;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.websockets.RemotePointService;

import javax.validation.constraints.NotNull;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class GameMechanics {

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMechanics.class);

    @NotNull
    private final UserDAO userDAO;

    @NotNull
    private final RemotePointService remotePointService;

    @NotNull
    private ConcurrentLinkedQueue<GamePlayer> waiters;

    public void addWaiter(GamePlayer waiter) {
        this.waiters.add(waiter);
    }

    public GameMechanics(@NotNull UserDAO userDAO, @NotNull RemotePointService remotePointService) {
        this.userDAO = userDAO;
        this.remotePointService = remotePointService;
        this.waiters = new ConcurrentLinkedQueue<>();
    }

    private void tryStartGame() {
        final Set<GamePlayer> matchedUsers = new LinkedHashSet<>();

        while (waiters.size() >= 2 || waiters.size() >= 1 && matchedUsers.size() >= 1) {
            final GamePlayer candidate = waiters.poll();

            matchedUsers.add(candidate);
            if (matchedUsers.size() == 2) {
                LOGGER.info("start game for 2 users");
                final Iterator<GamePlayer> iterator = matchedUsers.iterator();

            }
        }
    }

    public void gmStep() {
        tryStartGame();
    }
}
