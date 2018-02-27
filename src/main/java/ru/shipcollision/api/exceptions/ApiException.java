package ru.shipcollision.api.exceptions;

import org.springframework.http.HttpStatus;
import ru.shipcollision.api.models.ApiError;

import javax.validation.constraints.NotNull;

/**
 * Базовый класс иключений, которые должны приводить к демонстрации сообщений НЕ с 5ХХ-статусом.
 */
public class ApiException extends Exception {

    /**
     * Сообщениес ошибкой, которое будет показано клиенту.
     */
    private final ApiError errorResponse;

    /**
     * HTTP-статус ответа.
     */
    private final HttpStatus httpStatus;

    public ApiException() {
        super();
        this.errorResponse = new ApiError(getDefaultErrorMessage(), getDefaultErrorCode());
        this.httpStatus = getDefaultHttpStatus();
    }

    public ApiException(@NotNull String errorMessage) {
        super();
        this.errorResponse = new ApiError(errorMessage, getDefaultErrorCode());
        this.httpStatus = getDefaultHttpStatus();
    }

    ApiException(ApiException error) {
        super(error);
        this.errorResponse = error.errorResponse;
        this.httpStatus = error.httpStatus;
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

    public ApiError getResponse() {
        return errorResponse;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
