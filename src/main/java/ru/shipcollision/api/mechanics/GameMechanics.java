package ru.shipcollision.api.mechanics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.shipcollision.api.mechanics.base.Coordinates;
import ru.shipcollision.api.mechanics.models.Player;
import ru.shipcollision.api.mechanics.services.GameSessionService;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class GameMechanics {

    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger(GameMechanics.class);

    private final ConcurrentHashMap<Long, ConcurrentLinkedQueue<Player>> matchedUsers;

    private final @NotNull GameSessionService gameSessionService;

    private @NotNull ConcurrentLinkedQueue<Player> waiters;

    public GameMechanics(@NotNull GameSessionService gameSessionService) {
        this.waiters = new ConcurrentLinkedQueue<>();
        this.matchedUsers = new ConcurrentHashMap<>();
        this.gameSessionService = gameSessionService;
    }

    public void addWaiter(Player waiter) {
        this.waiters.add(waiter);
    }

    public void makeMove(@NotNull Coordinates coord, @NotNull Long playerId) {
        final GameSession session = gameSessionService.getPlayerSession(playerId);
        if (session != null && session.checkCoords(coord)) {
            session.makeMove(playerId, coord);
        }
    }

    private void tryStartGames() {
        while (waiters.size() >= 2 || waiters.size() >= 1 && matchedUsers.size() >= 1) {
            final Player candidate = waiters.poll();

            if (candidate == null) {
                break;
            }

            matchedUsers.computeIfAbsent(candidate.wantedRoomPlayers, user -> new ConcurrentLinkedQueue<>());
            matchedUsers.get(candidate.wantedRoomPlayers).add(candidate);

            for (Map.Entry<Long, ConcurrentLinkedQueue<Player>> entry : matchedUsers.entrySet()) {
                while (entry.getValue().size() >= entry.getKey()) {
                    final List<Player> queue = new ArrayList<>();

                    for (int i = 0; i < entry.getKey(); i++) {
                        queue.add(entry.getValue().poll());
                    }

                    LOGGER.info("Игра началась!!!");
                    gameSessionService.startGame(entry.getKey(), queue);
                }
            }
        }
    }

    void gmStep() {
        gameSessionService.syncSessions();
        tryStartGames();
    }
}
