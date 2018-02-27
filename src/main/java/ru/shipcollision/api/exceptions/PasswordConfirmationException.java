package ru.shipcollision.api.exceptions;

/**
 * Ошибка подтверждения пароля.
 */
public class PasswordConfirmationException extends ApiException {

    @Override
    protected String getDefaultErrorMessage() {
        return "Password confirmation failed";
    }

    @Override
    protected String getDefaultErrorCode() {
        return "password_confirmation";
    }
}
