package ru.shipcollision.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;

/**
 * Запрос на частичное обновление пользователя.
 */
@SuppressWarnings("PublicField")
public class UserPartialRequestEntity {

    @Nullable
    @JsonProperty("nickname")
    public String nickName;

    @Nullable
    @JsonProperty("email")
    public @Email String email;

    @Nullable
    @JsonProperty("password")
    public @Length(min = 6, message = "Password must be at least 6 characters") String password;

    @Nullable
    @JsonProperty("passwordConfirmation")
    public String confirmation;

    public boolean hasPassword() {
        return password != null;
    }

    public boolean passwordConfirmed() {
        return password != null && confirmation != null && password.equals(confirmation);
    }
}
