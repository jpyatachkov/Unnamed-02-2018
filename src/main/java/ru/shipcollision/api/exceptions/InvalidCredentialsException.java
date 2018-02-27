package ru.shipcollision.api.exceptions;

/**
 * Неверный логин или пароль.
 */
public class InvalidCredentialsException extends ApiException {

    public InvalidCredentialsException() {
        super();
    }

    public InvalidCredentialsException(ApiException error) {
        super(error);
    }

    @Override
    protected String getDefaultErrorMessage() {
        return "Invalid login or password";
    }

    @Override
    protected String getDefaultErrorCode() {
        return "invalid_credentials";
    }
}
