package ru.shipcollision.api.exceptions;

/**
 * Неверные параметры для сохранения.
 */
public class InvalidParamsException extends ApiException {

    public InvalidParamsException(String errorMessage) {
        super(errorMessage);
    }
}
