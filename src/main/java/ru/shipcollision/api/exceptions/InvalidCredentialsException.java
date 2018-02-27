package ru.shipcollision.api.exceptions;

/**
 * Неверный логин или пароль.
 */
public class InvalidCredentialsException extends ApiException {

    public InvalidCredentialsException() {
        super("Invalid login or password");
    }

    public InvalidCredentialsException(ApiException error) {
        super(error);
    }
}
