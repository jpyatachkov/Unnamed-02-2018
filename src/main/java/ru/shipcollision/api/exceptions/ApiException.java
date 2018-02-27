package ru.shipcollision.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.shipcollision.api.models.ApiError;

import javax.validation.constraints.NotNull;

/**
 * Базовый класс иключений, которые должны приводить к демонстрации сообщений НЕ с 5ХХ-статусом.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ApiException extends RuntimeException {

    /**
     * Сообщениес ошибкой, которое будет показано клиенту.
     */
    private final ApiError errorResponse;

    public ApiException() {
        super();
        this.errorResponse = new ApiError(getDefaultErrorMessage(), getDefaultErrorCode());
    }

    public ApiException(@NotNull String errorMessage) {
        super();
        this.errorResponse = new ApiError(errorMessage, getDefaultErrorCode());
    }

    ApiException(ApiException error) {
        super(error);
        this.errorResponse = error.errorResponse;
    }

    protected String getDefaultErrorMessage() {
        return "Error occured";
    }

    protected String getDefaultErrorCode() {
        return "error";
    }

    public ApiError getResponse() {
        return errorResponse;
    }
}
