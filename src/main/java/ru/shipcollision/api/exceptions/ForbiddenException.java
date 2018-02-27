package ru.shipcollision.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка авторизации.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends ApiException {

    @Override
    protected String getDefaultErrorMessage() {
        return "Forbidden";
    }

    @Override
    protected String getDefaultErrorCode() {
        return "forbidden";
    }
}
