package ru.shipcollision.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка авторизации.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends ApiException {

    public ForbiddenException() {
        super("Forbidden");
    }

    public ForbiddenException(ApiException error) {
        super(error);
    }
}
