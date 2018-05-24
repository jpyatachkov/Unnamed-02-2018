package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.websockets.Message;

import javax.validation.constraints.NotNull;

@SuppressWarnings("WeakerAccess")
public final class InfoMessage extends Message {

    @NotNull
    public final String type;

    @NotNull
    public String message;

    private InfoMessage(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public static InfoMessage createInfoMessage(String message) {
        return new InfoMessage(MessageLevelHelper.INFO, message);
    }

    public static InfoMessage createErrorMessage(String message) {
        return new InfoMessage(MessageLevelHelper.ERROR, message);
    }

    private static class MessageLevelHelper {
        public static final String INFO = "info";

        public static final String ERROR = "error";
    }
}
