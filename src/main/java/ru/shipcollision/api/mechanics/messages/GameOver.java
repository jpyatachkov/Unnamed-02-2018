package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.websockets.Message;

public class GameOver extends Message {

    public boolean win = false;
    public int score = 0;

    public GameOver(boolean win, int score) {
        this.win = win;
        this.score = score;
    }
}
