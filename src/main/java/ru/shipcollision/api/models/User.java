package ru.shipcollision.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Модель пользователя (игрока).
 */
@SuppressWarnings({"PublicField", "unused"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    private @Length(min = 6) @NotEmpty String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @Nullable Long id;

    @JsonProperty("username")
    private @Length(min = 6) @NotEmpty String username;

    @JsonProperty("email")
    private @Email @NotEmpty String email;

    @JsonProperty(value = "rank", access = JsonProperty.Access.READ_ONLY)
    private @Range(min = 0) int rank = 0;

    @JsonProperty(value = "avatarLink", access = JsonProperty.Access.READ_ONLY)
    @Nullable
    private @URL String avatarLink;

    public User() {

    }

    public User(@Length(min = 6) @NotEmpty String username,
                @Email @NotEmpty String email,
                @Length(min = 6) @NotEmpty String password) {
        this.username = username;
        this.email = email;
        setPassword(password);
    }

    public User(@Length(min = 6) @NotEmpty String username,
                @Email @NotEmpty String email,
                @Range(min = 0) int rank,
                @Length(min = 6) @NotEmpty String password) {
        this.username = username;
        this.email = email;
        this.rank = rank;
        setPassword(password);
    }

    public User(@Length(min = 6) @NotEmpty String username,
                @Email @NotEmpty String email,
                @Range(min = 0) int rank,
                @URL String avatarLink,
                @Length(min = 6) @NotEmpty String password) {
        this.username = username;
        this.email = email;
        this.rank = rank;
        this.avatarLink = avatarLink;
        setPassword(password);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }
}
