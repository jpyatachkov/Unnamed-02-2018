package ru.shipcollision.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.constraints.NotNull;

/**
 * Базовый класс иключений, которые должны приводить к демонстрации сообщений НЕ с 5ХХ-статусом.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ApiException extends RuntimeException {

    public ApiException(@NotNull String errorMessage) {
        super(errorMessage);
    }

    ApiException(ApiException error) {
        super(error);
    }
}
