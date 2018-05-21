package ru.shipcollision.api.mechanics.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import ru.shipcollision.api.mechanics.base.Cell;
import ru.shipcollision.api.models.User;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GamePlayer {
    @JsonIgnore
    public final Long id;
    public final String nickName;
    public int score;
    public List<List<Cell>> field;

    public GamePlayer(Long id, String nickName, List<List<Cell>> field) {
        this.id = id;
        this.nickName = nickName;
        this.score = 0;
        this.field = field;
    }

    public GamePlayer(User user, List<List<Cell>> field) {
        this.id = user.id;
        this.nickName = user.username;
        this.score = 0;
        this.field = field;
    }
}
