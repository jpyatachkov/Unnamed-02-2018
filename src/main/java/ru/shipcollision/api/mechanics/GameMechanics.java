package ru.shipcollision.api.mechanics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shipcollision.api.dao.UserDAO;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.websockets.RemotePointService;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameMechanics {

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMechanics.class);

    @NotNull
    private final UserDAO userDAO;

    @NotNull
    private final RemotePointService remotePointService;

    @NotNull
    private ConcurrentLinkedQueue<Long> waiters;

    public GameMechanics(@NotNull UserDAO userDAO, @NotNull RemotePointService remotePointService) {
        this.userDAO = userDAO;
        this.remotePointService = remotePointService;
        this.waiters = new ConcurrentLinkedQueue<>();
    }

    private void tryStartGame() {
        final Set<User> matchedUsers = new LinkedHashSet<>();

        while (waiters.size() >= 2 || waiters.size() >= 1 && matchedUsers.size() >= 1) {
            final Long candidate = waiters.poll();

            matchedUsers.add(userDAO.findById(candidate));
            if (matchedUsers.size() == 2) {
                //TODO:create session
            }
        }
    }
}
