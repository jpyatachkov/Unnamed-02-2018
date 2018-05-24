package ru.shipcollision.api.websockets;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.shipcollision.api.mechanics.messages.InfoMessage;
import ru.shipcollision.api.mechanics.messages.JoinGame;
import ru.shipcollision.api.mechanics.messages.MakeMove;
import ru.shipcollision.api.mechanics.messages.MoveDone;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({@JsonSubTypes.Type(MakeMove.class),
        @JsonSubTypes.Type(JoinGame.class),
        @JsonSubTypes.Type(MoveDone.class),
        @JsonSubTypes.Type(InfoMessage.class)})
public abstract class Message {
}
