package ru.shipcollision.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.shipcollision.api.exceptions.ApiException;

/**
 * Кастомный обработчик исключений.
 */
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity handleApiException(ApiException error) {
        return ResponseEntity.status(error.getHttpStatus()).body(error.getExceptionMessage());
    }
}
