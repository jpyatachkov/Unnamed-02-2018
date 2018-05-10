package ru.shipcollision.api.websockets;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.shipcollision.api.mechanics.messages.CreateRoom;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({@JsonSubTypes.Type(CreateRoom.Request.class)})
public abstract class Message {
}
