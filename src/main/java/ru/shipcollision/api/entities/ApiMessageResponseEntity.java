package ru.shipcollision.api.entities;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Ответ API об успешном окончании действия с API.
 */
@SuppressWarnings("PublicField")
public class ApiMessageResponseEntity {

    /**
     * Описание успешно выполненного действия.
     */
    @JsonProperty("message")
    public @NotNull String message;

    public ApiMessageResponseEntity(@NotNull String message) {
        this.message = message;
    }
}
