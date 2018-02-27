package ru.shipcollision.api.models;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Ответ API об успешном окончании действия с API.
 */
@SuppressWarnings("PublicField")
public class ApiMessage {

    /**
     * Описание успешно выполненного действия.
     */
    @JsonProperty("message")
    public @NotNull String message;

    public ApiMessage(@NotNull String message) {
        this.message = message;
    }
}
