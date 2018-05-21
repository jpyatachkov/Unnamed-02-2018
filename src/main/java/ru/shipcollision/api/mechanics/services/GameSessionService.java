package ru.shipcollision.api.mechanics.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.shipcollision.api.mechanics.GameSession;
import ru.shipcollision.api.mechanics.models.GamePlayer;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class GameSessionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSessionService.class);

    @NotNull
    private final Set<GameSession> gameSessions = new LinkedHashSet<>();

    public void StartGame(@NotNull Long count, @NotNull List<GamePlayer> players) {
        final GameSession session = new GameSession(count, players);
        gameSessions.add(session);
    }
}
