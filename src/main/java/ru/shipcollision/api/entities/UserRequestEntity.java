package ru.shipcollision.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * Запрос на создание пользователя.
 */
@SuppressWarnings("PublicField")
public class UserRequestEntity {

    @JsonProperty("nickname")
    public @NotEmpty String nickName;

    @JsonProperty("email")
    public @Email @NotEmpty String email;

    @JsonProperty("password")
    public @Length(min = 6, message = "Password must be at least 6 characters") @NotEmpty String password;

    @JsonProperty("passwordConfirmation")
    public @NotEmpty String confirmation;

    public boolean passwordConfirmed() {
        return password.equals(confirmation);
    }
}
