package ru.shipcollision.api.mechanics.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import ru.shipcollision.api.mechanics.base.Cell;
import ru.shipcollision.api.mechanics.messages.JoinGame;
import ru.shipcollision.api.models.User;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GamePlayer {
    @JsonIgnore
    public final Long id;
    public final String nickName;
    public int score;
    public List<List<Cell>> field;
    public @NotNull Long room;
    public int shipCount;

    public GamePlayer(Long id, String nickName, JoinGame message, int shipCount) {
        this.id = id;
        this.nickName = nickName;
        this.score = 0;
        this.field = message.field;
        this.room = message.count;
        this.shipCount = shipCount;
    }

    public GamePlayer(User user, JoinGame message, int shipCount) {
        this.id = user.id;
        this.nickName = user.username;
        this.score = 0;
        this.field = message.field;
        this.room = message.count;
        this.shipCount = shipCount;
    }
}
