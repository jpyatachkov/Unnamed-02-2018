package ru.shipcollision.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * Запрос на вход.
 */
@SuppressWarnings({"PublicField", "unused"})
public class SigninRequestEntity {

    @JsonProperty("email")
    public @Email @NotEmpty String email;

    @JsonProperty("password")
    public @NotEmpty String password;
}
