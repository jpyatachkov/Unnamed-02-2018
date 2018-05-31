package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.websockets.Message;

import javax.validation.constraints.NotNull;

/**
 * Информационное сообщение в игре.
 */
@SuppressWarnings("PublicField")
public final class GameMessage extends Message {

    public final @NotNull String type;

    public @NotNull String message;

    public GameMessage(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public static GameMessage createInfoMessage(String message) {
        return new GameMessage(MessageLevelHelper.INFO, message);
    }

    public static GameMessage createErrorMessage(String message) {
        return new GameMessage(MessageLevelHelper.ERROR, message);
    }

    private static class MessageLevelHelper {

        public static final String INFO = "info";

        public static final String ERROR = "error";
    }
}
