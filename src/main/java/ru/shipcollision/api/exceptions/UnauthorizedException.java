package ru.shipcollision.api.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Ошибка авторизации.
 */
public class UnauthorizedException extends ApiException {

    @Override
    protected String getDefaultErrorMessage() {
        return "User is unauthorized";
    }

    @Override
    protected String getDefaultErrorCode() {
        return "unauthorized";
    }

    @Override
    protected HttpStatus getDefaultHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
