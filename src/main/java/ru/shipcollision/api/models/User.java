package ru.shipcollision.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Модель пользователя (игрока).
 */
@SuppressWarnings({"PublicField", "unused"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public @NotNull Long id;

    @JsonProperty("username")
    public @Length(min = 6) @NotEmpty String username;

    @JsonProperty("email")
    public @Email @NotEmpty String email;

    @JsonProperty(value = "rank", access = JsonProperty.Access.READ_ONLY)
    public @Range(min = 0) int rank = 0;

    @JsonProperty(value = "avatarLink", access = JsonProperty.Access.READ_ONLY)
    @Nullable
    public @URL String avatarLink;

    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    public @Length(min = 6) @NotEmpty String password;

    public User() {
        this.id = ID_GENERATOR.getAndIncrement();
    }

    public User(@Length(min = 6) @NotEmpty String username,
                @Email @NotEmpty String email,
                @Length(min = 6) @NotEmpty String password) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(@Length(min = 6) @NotEmpty String username,
                @Email @NotEmpty String email,
                @Range(min = 0) int rank,
                @Length(min = 6) @NotEmpty String password) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.username = username;
        this.email = email;
        this.rank = rank;
        this.password = password;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !Objects.equals(getClass(), object.getClass())) {
            return false;
        }
        final User other = (User) object;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }
}
