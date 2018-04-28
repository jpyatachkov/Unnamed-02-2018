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
import java.util.Random;

/**
 * Модель пользователя (игрока).
 */
@SuppressWarnings({"PublicField", "unused"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public @Nullable Long id;

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

    private Long hashCode;

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

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !Objects.equals(getClass(), object.getClass())) {
            return false;
        }
        final User other = (User) object;

        if (id != null && other.id != null) {
            return Objects.equals(id, other.id);
        } else if (hashCode != null && other.hashCode != null) {
            return Objects.equals(hashCode, other.hashCode);
        } else {
            return rank == other.rank &&
                    Objects.equals(username, other.username) &&
                    Objects.equals(email, other.email) &&
                    Objects.equals(password, other.password) &&
                    Objects.equals(avatarLink, other.avatarLink);
        }
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.intValue();
        } else if (hashCode != null) {
            return hashCode.intValue();
        } else {
            final Random random = new Random();
            hashCode = random.nextLong();
            return hashCode.intValue();
        }
    }
}
