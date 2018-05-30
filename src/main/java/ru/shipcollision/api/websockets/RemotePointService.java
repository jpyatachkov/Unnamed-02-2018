package ru.shipcollision.api.websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.shipcollision.api.exceptions.ApiException;
import ru.shipcollision.api.mechanics.models.Player;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RemotePointService {
    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public RemotePointService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    void registerUser(@NotNull Long userId, @NotNull WebSocketSession webSocketSession) {
        if (sessions.containsKey(userId)) {
            throw new ApiException("Пользователь уже играет");
        }

        sessions.put(userId, webSocketSession);
    }

    public boolean isConnected(@NotNull Long userId) {
        return sessions.containsKey(userId) && sessions.get(userId).isOpen();
    }

    void removeUser(@NotNull Long userId) {
        sessions.remove(userId);
    }

    public void sendMessageToUser(@NotNull Long userId, @NotNull Message message) throws IOException {
        final WebSocketSession webSocketSession = sessions.get(userId);
        if (webSocketSession == null) {
            throw new IOException("no game websocket for user " + userId);
        }
        if (!webSocketSession.isOpen()) {
            throw new IOException("session is closed or not exists");
        }

        try {
            webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (JsonProcessingException e) {
            throw new IOException("Unable to send message", e);
        }
    }

    public void sendMessageToUser(@NotNull Player player, @NotNull Message message) throws IOException {
        sendMessageToUser(player.getUserId(), message);
    }
}
