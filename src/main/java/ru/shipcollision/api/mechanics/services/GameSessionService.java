package ru.shipcollision.api.mechanics.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.shipcollision.api.mechanics.GameSession;
import ru.shipcollision.api.mechanics.models.GamePlayer;
import ru.shipcollision.api.websockets.RemotePointService;

import javax.validation.constraints.NotNull;
import java.util.*;

@Service
public class GameSessionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSessionService.class);

    @NotNull
    private final Set<GameSession> gameSessions = new LinkedHashSet<>();

    // key = player-id, value = session
    @NotNull
    private final Map<Long, GameSession> usersMap = new HashMap<>();

    @NotNull
    private final RemotePointService remotePointService;

    public GameSessionService(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    public void deleteUserSession(Long playerId) {
        usersMap.remove(playerId);
    }

    // start new game
    public void startGame(@NotNull Long count, @NotNull List<GamePlayer> players) {
        final GameSession session = new GameSession(count.intValue(), players, remotePointService, this);
        for (GamePlayer player : players) {
            usersMap.put(player.id, session);
        }
        gameSessions.add(session);
        session.startTime();
    }

    // end session, delete session from list
    public void endSession() {
        for (GameSession session : gameSessions) {
            if (session.isFinished()) {
                gameSessions.remove(session);
            }
        }
    }

    // check running games
    public void checkInitGames() {

        for (GameSession session : gameSessions) {
            if (!session.isFinished()) {
                session.sync();
                LOGGER.info("Проверяем состояние игры");
            } else {
                endSession();
                LOGGER.info("Игра закончена");
            }
        }
    }

    public GameSession getPlayerSession(Long userId) {
        // TODO: обработать исключение, если данного пользователя нет в мапе, т.е. он не состоит в игровой сессии.
        return usersMap.get(userId);
    }
}
