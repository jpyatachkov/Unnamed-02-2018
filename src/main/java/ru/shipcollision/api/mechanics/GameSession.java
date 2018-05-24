package ru.shipcollision.api.mechanics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shipcollision.api.mechanics.base.Cell;
import ru.shipcollision.api.mechanics.base.Coordinates;
import ru.shipcollision.api.mechanics.messages.InfoMessage;
import ru.shipcollision.api.mechanics.messages.MoveDone;
import ru.shipcollision.api.mechanics.models.GamePlayer;
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

    @NotNull
    private final Long sessionId;

    private List<GamePlayer> players;

    private int countPlayers;

    private boolean isFinished;

    private int currentPlayerIdx;

    @NotNull
    private Timestamp endMoveTime;

    @NotNull
    private RemotePointService remotePointService;

    public GameSession(int countPlayers, List<GamePlayer> array, RemotePointService remotePointService) {
        this.sessionId = ID_GENERATOR.getAndIncrement();
        this.countPlayers = countPlayers;
        this.players = array;
        this.isFinished = false;
        this.currentPlayerIdx = 0;
        this.remotePointService = remotePointService;
    }

    private int checkDimension(int count) {
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

    public void startTime() {
        this.endMoveTime = new Timestamp(System.currentTimeMillis() + 20);
    }

    @NotNull
    public Long getSessionId() {
        return sessionId;
    }

    @NotNull
    public int getCountPlayers() {
        return countPlayers;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setEndMoveTime(Timestamp endMoveTime) {
        this.endMoveTime = endMoveTime;
    }

    public void sync() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        if (endMoveTime.before(currentTime)) {
            nextPlayer();
        }
    }

    private void nextPlayer() {
        currentPlayerIdx = currentPlayerIdx + 1 % countPlayers;
        startTime();
        try {
            //TODO: переделать сообщение, включить сцену.
            remotePointService.sendMessageToUser(players.get(currentPlayerIdx).id, InfoMessage.createInfoMessage("Твой ход"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GamePlayer getCurrentPlayer() {
        return players.get(currentPlayerIdx);
    }

    boolean checkCoords(Coordinates coord) {
        int dimension = checkDimension(countPlayers);
        return (coord.getI() <= dimension && coord.getJ() <= dimension);
    }

    private boolean checkCurrentPlayer(@NotNull Long playerId) {
        return (playerId.equals(getCurrentPlayer().id));
    }

    void makeMove(Long playerId, Coordinates coord) {
        if (checkCurrentPlayer(playerId)) {
            //стреляем в поле игрока
            MoveResult result = new MoveResult();
            GamePlayer currentPlayer = getCurrentPlayer();
            Cell cell = currentPlayer.field.get(coord.getI()).get(coord.getJ());
            if (cell == Cell.EMPTY || cell == Cell.BYSY) {
                for (GamePlayer player : players) {
                    result.messages.computeIfAbsent(player.id, messages -> new ArrayList<>());
                    makeShot(player, coord, result);
                }
                currentPlayer.score += result.destroyedShipCount;

                if (result.isDestroyedSelf && result.destroyedShipCount != 0) {
                    //попал по себе и по другим
                    currentPlayer.field.get(coord.getI()).set(coord.getJ(), Cell.DESTROYED_OTHER);
                    result.messages.get(currentPlayer.id).add(new MoveDone("Попадание", coord,
                            Cell.DESTROYED_OTHER, currentPlayer.score));
                }

                if (!result.isDestroyedSelf && result.destroyedShipCount == 0) {
                    //не попал никуда
                    currentPlayer.field.get(coord.getI()).set(coord.getJ(), Cell.MISSED);
                    result.messages.get(currentPlayer.id).add(new MoveDone("Промах", coord,
                            Cell.MISSED, currentPlayer.score));
                }
            } else {
                result.messages.get(currentPlayer.id).add(InfoMessage.createErrorMessage("Сюда нельзя ходить"));
            }
        }
    }

    private void makeShot(GamePlayer player, Coordinates coord, MoveResult result) {
        Cell currentCell = player.field.get(coord.getI()).get(coord.getJ());
        switch (currentCell) {
            case BYSY:
                player.field.get(coord.getI()).set(coord.getJ(), Cell.DESTROYED);
                if (player.equals(getCurrentPlayer())) {
                    result.isDestroyedSelf = true;
                } else {
                    result.messages.get(player.id).add(InfoMessage.createInfoMessage("По вам попали"));
                    result.destroyedShipCount++;
                }
        }
    }

    private static class MoveResult {
        boolean isDestroyedSelf = false;
        int destroyedShipCount = 0;
        Map<Long, List<Message>> messages = new ConcurrentHashMap<>();
    }
}
