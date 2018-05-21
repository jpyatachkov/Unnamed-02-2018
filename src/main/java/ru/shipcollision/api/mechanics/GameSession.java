package ru.shipcollision.api.mechanics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shipcollision.api.mechanics.models.GamePlayer;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class GameSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSession.class);
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    @NotNull
    private final Long sessionId;

    private List<GamePlayer> players;

    private Long countPlayers;

    private boolean isFinished;

    public GameSession(Long countPlayers, List<GamePlayer> array) {
        this.sessionId = ID_GENERATOR.getAndIncrement();
        this.countPlayers = countPlayers;
        this.players = array;
        this.isFinished = false;
    }

    @NotNull
    public Long getSessionId() { return sessionId; }

    @NotNull
    public Long getCountPlayers() { return countPlayers; }
}
