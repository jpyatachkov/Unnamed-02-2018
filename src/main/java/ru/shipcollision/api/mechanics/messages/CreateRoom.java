package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.websockets.Message;

import java.util.List;
import java.util.Map;

public class CreateRoom {
    public static final class Request extends Message {
        public int count;
        public Map<Integer, List<List<Integer>>> playerField;
    }
}
