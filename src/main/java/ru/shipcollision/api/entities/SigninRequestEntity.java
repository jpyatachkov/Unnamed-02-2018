package ru.shipcollision.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * Запрос на вход.
 */
public class SigninRequestEntity {

    @JsonProperty("email")
    private @Email @NotEmpty String email;

    @JsonProperty("password")
    private @NotEmpty String password;

    @SuppressWarnings("unused")
    public SigninRequestEntity() {
    }

    @SuppressWarnings("unused")
    public SigninRequestEntity(@NotEmpty String email, @NotEmpty String password) {
        this.email = email;
        this.password = password;
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
}
