package ru.shipcollision.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * Запрос на создание пользователя.
 */
public class UserRequestEntity {

    @JsonProperty("nickname")
    private @NotEmpty String nickName;

    @JsonProperty("email")
    private @Email @NotEmpty String email;

    @JsonProperty("password")
    private @Length(min = 6, message = "Password must be at least 6 characters") @NotEmpty String password;

    @JsonProperty("passwordConfirmation")
    private @NotEmpty String confirmation;

    @SuppressWarnings("unused")
    public UserRequestEntity() {
    }

    @SuppressWarnings("unused")
    public UserRequestEntity(@NotEmpty String nickName,
                             @NotEmpty String email,
                             @NotEmpty String password,
                             @NotEmpty String confirmation) {
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.confirmation = confirmation;
    }

    public boolean passwordConfirmed() {
        return password.equals(confirmation);
    }

    @SuppressWarnings("unused")
    public String getNickName() {
        return nickName;
    }

    @SuppressWarnings("unused")
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }

    @SuppressWarnings("unused")
    public void setEmail(String email) {
        this.email = email;
    }

    @SuppressWarnings("unused")
    public String getPassword() {
        return password;
    }

    @SuppressWarnings("unused")
    public void setPassword(String password) {
        this.password = password;
    }

    @SuppressWarnings("unused")
    public String getConfirmation() {
        return confirmation;
    }

    @SuppressWarnings("unused")
    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }
}
