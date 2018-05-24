package ru.shipcollision.api.mechanics;

import ru.shipcollision.api.mechanics.models.GamePlayer;

import javax.validation.constraints.NotNull;

public class MechanicsPart {
    private int score;
    @NotNull
    private GamePlayer player;

    public MechanicsPart(int score, @NotNull GamePlayer player) {
        this.score = score;
        this.player = player;
    }

    public int getScore() {
        return score;
    }

    public void CheckShot() {

    }
}
