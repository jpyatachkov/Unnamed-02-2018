package ru.shipcollision.api.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Ошибка авторизации.
 */
public class ForbiddenException extends ApiException {

    @Override
    protected String getDefaultErrorMessage() {
        return "Forbidden";
    }

    @Override
    protected String getDefaultErrorCode() {
        return "forbidden";
    }

    @Override
    protected HttpStatus getDefaultHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
