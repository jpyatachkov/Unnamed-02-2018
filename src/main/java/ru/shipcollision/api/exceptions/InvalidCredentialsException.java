package ru.shipcollision.api.exceptions;

import javax.validation.constraints.NotNull;

/**
 * Неверный логин или пароль.
 */
public class InvalidCredentialsException extends ApiException {

    @Override
    protected String getDefaultErrorMessage() {
        return "Invalid login or password";
    }

    @Override
    protected String getDefaultErrorCode() {
        return "invalid_credentials";
    }
}
