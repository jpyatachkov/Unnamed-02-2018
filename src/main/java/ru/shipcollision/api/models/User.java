package ru.shipcollision.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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

    @JsonProperty(value = "rank", access = JsonProperty.Access.READ_ONLY)
    public int rank = 0;

    @JsonProperty(value = "avatarLink", access = JsonProperty.Access.READ_ONLY)
    @Nullable
    public String avatarLink;

    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    public @NotEmpty String passwordHash;

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
