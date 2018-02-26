package ru.shipcollision.api.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import ru.shipcollision.api.entities.ApiErrorResponseEntiry;

import javax.validation.constraints.NotNull;

/**
 * Базовый класс иключений, которые должны приводить к демонстрации сообщений НЕ с 5ХХ-статусом.
 */
public class ApiException extends Exception {

    /**
     * Сообщениес ошибкой, которое будет показано клиенту.
     */
    private final ApiErrorResponseEntiry errorResponse;

    /**
     * HTTP-статус ответа.
     */
    private final HttpStatus httpStatus;

    public ApiException() {
        super();
        this.errorResponse = new ApiErrorResponseEntiry(getDefaultErrorMessage(), getDefaultErrorCode());
        this.httpStatus = getDefaultHttpStatus();
    }

    public ApiException(@NotNull String errorMessage) {
        super();
        this.errorResponse = new ApiErrorResponseEntiry(errorMessage, getDefaultErrorCode());
        this.httpStatus = getDefaultHttpStatus();
    }

    public ApiException(Exception exception) {
        super(exception);
        this.errorResponse = new ApiErrorResponseEntiry(exception.getMessage(), getDefaultErrorCode());
        this.httpStatus = getDefaultHttpStatus();
    }

    protected String getDefaultErrorMessage() {
        return "Error occured";
    }

    protected String getDefaultErrorCode() {
        return "error";
    }

    protected HttpStatus getDefaultHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    public ApiErrorResponseEntiry getResponse() {
        return errorResponse;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
