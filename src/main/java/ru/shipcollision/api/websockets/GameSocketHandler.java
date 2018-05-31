package ru.shipcollision.api.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.shipcollision.api.exceptions.ApiException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.services.SessionService;

import javax.validation.constraints.NotNull;
import java.io.IOException;

import static org.springframework.web.socket.CloseStatus.SERVER_ERROR;

@Component
public class GameSocketHandler extends TextWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameSocketHandler.class);

    private static final CloseStatus ACCESS_DENIED = new CloseStatus(4500, "Not logged in. Access denied");

    private final @NotNull MessageHandlerContainer messageHandlerContainer;

    private final @NotNull RemotePointService remotePointService;

    private final @NotNull SessionService sessionService;

    private final ObjectMapper objectMapper;

    public GameSocketHandler(@NotNull MessageHandlerContainer messageHandlerContainer,
                             @NotNull RemotePointService remotePointService,
                             ObjectMapper objectMapper,
                             @NotNull SessionService sessionService) {
        this.messageHandlerContainer = messageHandlerContainer;
        this.remotePointService = remotePointService;
        this.objectMapper = objectMapper;
        this.sessionService = sessionService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        try {
            final User user = sessionService.wsGetUserFromSession(webSocketSession);
            remotePointService.registerUser(user.id, webSocketSession);
        } catch (NotFoundException e) {
            LOGGER.warn("User requested websocket is not registred or not logged in. Openning websocket session is denied.");
            closeSessionSilently(webSocketSession, ACCESS_DENIED);
        } catch (ApiException e) {
            LOGGER.warn("User already plays");
            closeSessionSilently(webSocketSession, ACCESS_DENIED);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) {
        if (!webSocketSession.isOpen()) {
            return;
        }

        try {
            final User user = sessionService.wsGetUserFromSession(webSocketSession);
            handleMessage(user, message);
        } catch (NotFoundException e) {
            closeSessionSilently(webSocketSession, ACCESS_DENIED);
        }
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    private void handleMessage(User userProfile, TextMessage text) {
        final Message message;
        try {
            message = objectMapper.readValue(text.getPayload(), Message.class);
        } catch (IOException ex) {
            LOGGER.error("wrong json format at game response", ex);
            return;
        }
        try {
            messageHandlerContainer.handle(message, userProfile.id);
        } catch (Throwable e) {
            LOGGER.error("Can't handle message of type " + message.getClass().getName() + " with content: " + text, e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
        LOGGER.warn("Websocket transport problem", throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        final Long userId = (Long) webSocketSession.getAttributes().get("userId");
        if (userId == null) {
            LOGGER.warn("User disconnected but his session was not found (closeStatus=" + closeStatus + ')');
            return;
        }
        remotePointService.removeUser(userId);
    }

    @SuppressWarnings("SameParameterValue")
    private void closeSessionSilently(@NotNull WebSocketSession session, @Nullable CloseStatus closeStatus) {
        final CloseStatus status = closeStatus == null ? SERVER_ERROR : closeStatus;
        try {
            session.close(status);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            LOGGER.warn("Session terminated");
        }
    }
}
