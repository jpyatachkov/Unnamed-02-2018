package ru.shipcollision.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends ApiException {

    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    protected String getDefaultErrorMessage() {
        return "Not Found";
    }

    @Override
    protected String getDefaultErrorCode() {
        return "not_found";
    }
}
