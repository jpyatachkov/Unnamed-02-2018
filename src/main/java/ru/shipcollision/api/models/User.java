package ru.shipcollision.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * Модель пользователя (игрока).
 */
@SuppressWarnings({"PublicField", "unused"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    @Nullable
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long id;

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

    }

    public User(@Length(min = 6) @NotEmpty String username,
                @Email @NotEmpty String email,
                @Length(min = 6) @NotEmpty String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(@Length(min = 6) @NotEmpty String username,
                @Email @NotEmpty String email,
                @Range(min = 0) int rank,
                @Length(min = 6) @NotEmpty String password) {
        this.username = username;
        this.email = email;
        this.rank = rank;
        this.password = password;
    }

    public User(@Nullable Long id,
                @Length(min = 6) @NotEmpty String username,
                @Email @NotEmpty String email,
                @Range(min = 0) int rank,
                @URL String avatarLink,
                @Length(min = 6) @NotEmpty String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.rank = rank;
        this.avatarLink = avatarLink;
        this.password = password;
    }

    public User(User other) {
        this.id = other.id;
        this.username = other.username;
        this.email = other.email;
        this.rank = other.rank;
        this.avatarLink = other.avatarLink;
        this.password = other.password;
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

        //noinspection OverlyComplexBooleanExpression
        return Objects.equals(id, other.id)
                && Objects.equals(username, other.username)
                && Objects.equals(email, other.email)
                && Objects.equals(rank, other.rank)
                && Objects.equals(avatarLink, other.avatarLink);
    }

    @Override
    public int hashCode() {
        final int prime = 13;

        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + rank ^ (rank >>> 28);
        result = prime * result + ((avatarLink == null) ? 0 : avatarLink.hashCode());
        return result;
    }
}
