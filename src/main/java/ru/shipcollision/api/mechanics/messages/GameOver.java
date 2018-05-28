package ru.shipcollision.api.mechanics.messages;

import ru.shipcollision.api.websockets.Message;

/**
 * Сообщение об окончании игры.
 */
@SuppressWarnings("PublicField")
public class GameOver extends Message {

    public boolean win;

    public int score;

    public GameOver(boolean win, int score) {
        this.win = win;
        this.score = score;
    }
}
