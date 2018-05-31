package ru.shipcollision.api.exceptions;

/**
 * Ошибка пагинации.
 */
public class PaginationException extends ApiException {

    public PaginationException() {
        super("Неверный номер страницы или параметры пагинации");
    }

    public PaginationException(String errorMessage) {
        super(errorMessage);
    }
}
