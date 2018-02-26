package ru.shipcollision.api.exceptions;

import javax.validation.constraints.NotNull;

/**
 * Ошибка пагинации.
 */
public class PaginationException extends ApiException {

    public PaginationException() {
        super();
    }

    public PaginationException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    protected String getDefaultErrorMessage() {
        return "Invalid page number or elements limit";
    }

    @Override
    protected String getDefaultErrorCode() {
        return "pagination_error";
    }
}
