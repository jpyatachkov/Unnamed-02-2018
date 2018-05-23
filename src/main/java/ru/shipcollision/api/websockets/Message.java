package ru.shipcollision.api.websockets;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.shipcollision.api.mechanics.messages.JoinGame;
import ru.shipcollision.api.mechanics.messages.MakeMoove;
import ru.shipcollision.api.mechanics.messages.MoveDone;
import ru.shipcollision.api.mechanics.messages.StartMove;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({@JsonSubTypes.Type(MakeMoove.class),
                @JsonSubTypes.Type(JoinGame.class),
                @JsonSubTypes.Type(MoveDone.class),
                @JsonSubTypes.Type(StartMove.class)})
public abstract class Message {
}
