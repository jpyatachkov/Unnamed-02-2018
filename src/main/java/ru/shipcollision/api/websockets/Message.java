package ru.shipcollision.api.websockets;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.shipcollision.api.mechanics.messages.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(EnableScene.class),
        @JsonSubTypes.Type(ErrorMessage.class),
        @JsonSubTypes.Type(GameMessage.class),
        @JsonSubTypes.Type(GameOver.class),
        @JsonSubTypes.Type(JoinGame.class),
        @JsonSubTypes.Type(RequestGamePermission.class),
        @JsonSubTypes.Type(MoveDone.class)})
public abstract class Message {
}
