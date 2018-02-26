package ru.shipcollision.api.entities;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Ответ API об успешном окончании действия с API.
 */
public class ApiMessageResponseEntity {

    /**
     * Описание успешно выполненного действия.
     */
    @JsonProperty("message")
    private @NotNull String message;

    public ApiMessageResponseEntity(@NotNull String message) {
        this.message = message;
    }

    @SuppressWarnings("unused")
    public String getMessage() {
        return message;
    }

    @SuppressWarnings("unused")
    public void setMessage(@NotNull String message) {
        this.message = message;
    }
}
