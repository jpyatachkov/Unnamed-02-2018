package ru.shipcollision.api.mechanics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shipcollision.api.mechanics.base.CellStatus;
import ru.shipcollision.api.mechanics.base.Coordinates;
import ru.shipcollision.api.mechanics.messages.*;
import ru.shipcollision.api.mechanics.models.Player;
import ru.shipcollision.api.mechanics.services.GameSessionService;
import ru.shipcollision.api.websockets.Message;
import ru.shipcollision.api.websockets.RemotePointService;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class GameSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameSession.class);

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    private final @NotNull Long sessionId;
    private final @NotNull RemotePointService remotePointService;
    private final @NotNull GameSessionService gameSessionService;
    private List<Player> players;
    private int playersCount;
    private int fieldDim;
    private boolean isFinished;
    private int currentPlayerIdx;
    private @NotNull Timestamp endMoveTime;

    public GameSession(List<Player> players,
                       RemotePointService remotePointService,
                       GameSessionService gameSessionService) {
        this.sessionId = ID_GENERATOR.getAndIncrement();
        this.players = players;
        this.playersCount = players.size();
        this.fieldDim = GameRulesHelper.getFieldDimForPlayers(this.playersCount);
        this.isFinished = false;
        this.currentPlayerIdx = 0;
        this.remotePointService = remotePointService;
        this.gameSessionService = gameSessionService;
    }

    public void notifyUsersOnStarted() throws IOException {
        for (Player player : players) {
            remotePointService.sendMessageToUser(player.getUserId(), new GameStarted());
        }
    }

    public void startTime() {
        this.endMoveTime = new Timestamp(System.currentTimeMillis() + GameRulesHelper.MAX_SECONDS_TO_MOVE);
        try {
            remotePointService.sendMessageToUser((long) currentPlayerIdx, new EnableScene());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void sync() {
        final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        if (endMoveTime.before(currentTime)) {
            nextPlayer();
        }
    }

    void finishGameForPlayer(Player player, MoveResult result) {
        players.remove(player);
        this.playersCount--;

        gameSessionService.deleteUserSession(player.user.id);

        final GameOver gameOver = new GameOver(false, result.destroyedShipCount);
        result.addMessageFor(player, gameOver);
    }

    void makeMove(Long playerId, Coordinates coords) {

        if (isCurrentPlayer(playerId)) {
            final MoveResult result = new MoveResult();
            final Player currentPlayer = getCurrentPlayer();
            final CellStatus cell = currentPlayer.getCellStatus(coords);

            boolean shallGoNext = false;
            if (cell == CellStatus.EMPTY || cell == CellStatus.BUSY) {
                for (Player player : players) {
                    if (player.shipsCount == 0) {
                        finishGameForPlayer(player, result);
                        continue;
                    }
                    result.messages.computeIfAbsent(player.getUserId(), messages -> new ArrayList<>());
                    makeShot(player, coords, result);
                }
                currentPlayer.score += result.destroyedShipCount;

                if (result.isDestroyedSelf && result.destroyedShipCount != 0) {
                    // попал по себе и по другим.
                    currentPlayer.setCellStatus(coords, CellStatus.DESTROYED_OTHER);

                    final MoveDone moveDone = new MoveDone(
                            "Попадание",
                            coords,
                            CellStatus.DESTROYED_OTHER,
                            currentPlayer.score
                    );
                    result.addMessageFor(currentPlayer, moveDone);
                }

                if (!result.isDestroyedSelf && result.destroyedShipCount == 0) {
                    // не попал никуда
                    currentPlayer.setCellStatus(coords, CellStatus.MISSED);

                    final MoveDone moveDone = new MoveDone(
                            "Промах",
                            coords,
                            CellStatus.MISSED,
                            currentPlayer.score
                    );
                    result.addMessageFor(currentPlayer, moveDone);
                }

                shallGoNext = true;
            } else {
                result.addMessageFor(currentPlayer, GameMessage.createErrorMessage("Сюда нельзя ходить"));
            }

            for (Map.Entry<Long, List<Message>> messageEntry : result.messages.entrySet()) {
                final Long userId = messageEntry.getKey();
                final List<Message> messagesForUser = messageEntry.getValue();

                for (Message message : messagesForUser) {
                    try {
                        remotePointService.sendMessageToUser(userId, message);
                    } catch (IOException e) {
                        continue;
                    }
                }
            }

            if (shallGoNext) {
                nextPlayer();
            }
        }
    }

    boolean checkCoords(Coordinates coord) {
        return (coord.getI() <= fieldDim && coord.getJ() <= fieldDim);
    }

    Player getCurrentPlayer() {
        return players.get(currentPlayerIdx);
    }

    private void nextPlayer() {
        currentPlayerIdx = (currentPlayerIdx + 1) % playersCount;
        startTime();
        try {
            remotePointService.sendMessageToUser(getCurrentPlayer(), new EnableScene());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean isCurrentPlayer(@NotNull Player player) {
        final Player currentPlayer = getCurrentPlayer();
        return player.getUserId().equals(currentPlayer.getUserId());
    }

    private boolean isCurrentPlayer(@NotNull Long playerId) {
        final Player currentPlayer = getCurrentPlayer();
        return playerId.equals(currentPlayer.getUserId());
    }

    private void makeShot(Player player, Coordinates coords, MoveResult result) {
        final CellStatus currentCell = player.getCellStatus(coords);
        if (currentCell == CellStatus.BUSY) {
            player.shipsCount--;
            player.setCellStatus(coords, CellStatus.DESTROYED);

            if (isCurrentPlayer(player)) {
                result.isDestroyedSelf = true;
            } else {
                result.addMessageFor(player, GameMessage.createInfoMessage("По вам попали"));
                result.destroyedShipCount++;
            }
        }
    }

    private static class MoveResult {

        boolean isDestroyedSelf = false;

        int destroyedShipCount = 0;

        Map<Long, List<Message>> messages = new ConcurrentHashMap<>();

        void addMessageFor(Long userId, Message message) {
            messages.get(userId).add(message);
        }

        void addMessageFor(Player player, Message message) {
            addMessageFor(player.getUserId(), message);
        }
    }
}
