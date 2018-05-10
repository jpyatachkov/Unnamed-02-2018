package ru.shipcollision.api.mechanics.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import ru.shipcollision.api.models.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GamePlayer {
    @JsonIgnore
    public final Long id;
    public final String nickName;
    public int score;

    public GamePlayer(Long id, String nickName) {
        this.id = id;
        this.nickName = nickName;
        this.score = 0;
    }

    public GamePlayer(User user) {
        this.id = user.id;
        this.nickName = user.username;
        this.score = 0;
    }
}
