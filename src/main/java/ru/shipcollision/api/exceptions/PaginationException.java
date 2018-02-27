package ru.shipcollision.api.exceptions;

/**
 * Ошибка пагинации.
 */
public class PaginationException extends ApiException {

    public PaginationException() {
        super("Invalid page number or elements limit");
    }

    public PaginationException(String errorMessage) {
        super(errorMessage);
    }
}
