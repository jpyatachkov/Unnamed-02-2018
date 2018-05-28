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
import ru.shipcollision.api.dao.UserDAO;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.services.SessionServiceImpl;

import javax.validation.constraints.NotNull;
import java.io.IOException;

import static org.springframework.web.socket.CloseStatus.SERVER_ERROR;

@Component
public class GameSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSocketHandler.class);
    private static final CloseStatus ACCESS_DENIED = new CloseStatus(4500, "Not logged in. Access denied");

    @NotNull
    private final UserDAO userDAO;
    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;
    @NotNull
    private final RemotePointService remotePointService;
    @NotNull
    private final SessionServiceImpl sessionService;

    private final ObjectMapper objectMapper;

    public GameSocketHandler(@NotNull UserDAO userService, @NotNull MessageHandlerContainer messageHandlerContainer,
                             @NotNull RemotePointService remotePointService, ObjectMapper objectMapper,
                             @NotNull SessionServiceImpl sessionService) {
        this.userDAO = userService;
        this.messageHandlerContainer = messageHandlerContainer;
        this.remotePointService = remotePointService;
        this.objectMapper = objectMapper;
        this.sessionService = sessionService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {

        final Long id = sessionService.wsGetCurrentUserId(webSocketSession); // (Long) webSocketSession.getAttributes().get("userId");
        LOGGER.info("USER - ", id);
        if (id == null || userDAO.findById(id) == null) {
            LOGGER.warn("User requested websocket is not registred or not logged in. Openning websocket session is denied.");
            closeSessionSilently(webSocketSession, ACCESS_DENIED);
            return;
        }

        remotePointService.registerUser(id, webSocketSession);
    }

    @Override
    protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) {
        if (!webSocketSession.isOpen()) {
            return;
        }
        final Long id = sessionService.wsGetCurrentUserId(webSocketSession); //(Long) webSocketSession.getAttributes().get("userId");
        final User user;
        if (id == null || (user = userDAO.findById(id)) == null) {
            closeSessionSilently(webSocketSession, ACCESS_DENIED);
            return;
        }
        handleMessage(user, message);
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
        } catch (Exception ignore) {
            LOGGER.info("close session");
        }

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
