package ru.shipcollision.api.mechanics.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.shipcollision.api.mechanics.GameSession;
import ru.shipcollision.api.mechanics.models.Player;
import ru.shipcollision.api.websockets.RemotePointService;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;

@Service
public class GameSessionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameSessionService.class);

    private final @NotNull Set<GameSession> gameSessions = new LinkedHashSet<>();

    // key = player-id, value = session
    private final @NotNull Map<Long, GameSession> usersMap = new HashMap<>();

    private final @NotNull RemotePointService remotePointService;

    public GameSessionService(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    public void deleteUserSession(Long playerId) {
        usersMap.remove(playerId);
    }

    public void startGame(@NotNull Long count, @NotNull List<Player> players) {
        if (count.intValue() != players.size()) {
            LOGGER.error("Количество игроков в сессии не соответствует ожидаемому");
            return;
        }

        final GameSession session = new GameSession(players, remotePointService, this);

        for (Player player : players) {
            usersMap.put(player.getUserId(), session);
        }

        gameSessions.add(session);

        try {
            session.notifyUsersOnStarted();
        } catch (IOException e) {
            for (Player player : players) {
                usersMap.remove(player.getUserId());
            }
            gameSessions.remove(session);
            return;
        }

        session.startTime();
    }

    public void syncSessions() {
        for (GameSession session : gameSessions) {
            if (session.isFinished()) {
                LOGGER.info("Сессия удалена ", session.getSessionId());
                gameSessions.remove(session);
            } else {
                LOGGER.info("Состояние сессии обновлено ", session.getSessionId());
                session.sync();
            }
        }
    }

    public GameSession getPlayerSession(Long userId) {
        return usersMap.get(userId);
    }
}
