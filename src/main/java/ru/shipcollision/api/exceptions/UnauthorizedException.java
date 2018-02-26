package ru.shipcollision.api.exceptions;

import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;

/**
 * Ошибка авторизации.
 */
public class UnauthorizedException extends ApiException {

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(@NotNull String errorMessage) {
        super(errorMessage);
    }

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
        return HttpStatus.UNAUTHORIZED;
    }
}
