package ru.shipcollision.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;
import ru.shipcollision.api.entities.UserPartialRequestEntity;
import ru.shipcollision.api.entities.UserRequestEntity;
import ru.shipcollision.api.exceptions.InvalidCredentialsException;
import ru.shipcollision.api.exceptions.NotFoundException;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Модель пользователя (игрока).
 */
@SuppressWarnings({"PublicField", "unused"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends AbstractModel {

    @JsonProperty("nickname")
    public @NotNull String nickName;

    @JsonProperty("email")
    public @NotNull String email;

    @JsonProperty("rank")
    public int rank = 0;

    @JsonProperty("avatarLink")
    @Nullable
    public String avatarLink;

    @JsonIgnore
    public @NotNull String passwordHash;

    public User() {
        super();
    }

    public User(@NotNull String nickName, @NotNull String email, @NotNull String passwordHash) {
        super();
        this.nickName = nickName;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
