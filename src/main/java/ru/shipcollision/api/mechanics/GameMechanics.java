package ru.shipcollision.api.mechanics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.shipcollision.api.dao.UserDAO;
import ru.shipcollision.api.mechanics.base.Coordinates;
import ru.shipcollision.api.mechanics.models.GamePlayer;
import ru.shipcollision.api.mechanics.services.GameSessionService;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.websockets.RemotePointService;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private final GameSessionService gameSessionService;

    @NotNull
    private ConcurrentLinkedQueue<GamePlayer> waiters;

    public final ConcurrentHashMap<Long, ConcurrentLinkedQueue<GamePlayer>> matchedUsers;

    public void addWaiter(GamePlayer waiter) {
        this.waiters.add(waiter);
    }

    public GameMechanics(@NotNull UserDAO userDAO,
                         @NotNull RemotePointService remotePointService,
                         @NotNull GameSessionService gameSessionService) {
        this.userDAO = userDAO;
        this.remotePointService = remotePointService;
        this.waiters = new ConcurrentLinkedQueue<>();
        this.matchedUsers = new ConcurrentHashMap<>();
        this.gameSessionService = gameSessionService;
    }

    public void makeMove(@NotNull Coordinates coord,@NotNull Long playerId) {
        //TODO: обработка исключения NotFoundException
        //User user = userDAO.findById(playerId);
        GameSession session = gameSessionService.getPlayerSession(playerId);
        if (session.checkCoords(coord)) {
            session.makeMove(playerId, coord);
        }
    }

    private void tryStartGame() {

        while (waiters.size() >= 2 || waiters.size() >= 1 && matchedUsers.size() >= 1) {
            final GamePlayer candidate = waiters.poll();

            if (candidate == null) {
                break;
            }

            matchedUsers.computeIfAbsent(candidate.room, user -> new ConcurrentLinkedQueue<>());
            matchedUsers.get(candidate.room).add(candidate);


            for (Map.Entry<Long, ConcurrentLinkedQueue<GamePlayer>> entry : matchedUsers.entrySet()) {
//                ConcurrentLinkedQueue<GamePlayer> queue = entry.getValue();
                while (entry.getValue().size() >= entry.getKey()) {
                    List<GamePlayer> queue = new ArrayList<>();
                    for (int i = 0; i < entry.getKey(); i++) {
                        queue.add(entry.getValue().poll());
                    }
                    LOGGER.info("Игра началась!!!");
                    gameSessionService.startGame(entry.getKey(), queue);

                }
            }
        }
    }

    public void gmStep() {

        gameSessionService.checkInitGames();

        tryStartGame();
    }

    public int getShipsCount(int count) {
        switch (count) {
            case 2:
                return 10;
            case 3:
                return 15;
            case 4:
                return 20;
            default:
                return 0;
        }
    }
}
