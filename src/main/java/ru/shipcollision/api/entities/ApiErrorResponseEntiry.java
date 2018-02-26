package ru.shipcollision.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Ответ API с описанием произошедшей ошибки.
 */
@SuppressWarnings("PublicField")
public class ApiErrorResponseEntiry {

    /**
     * Описание ошибки.
     */
    @JsonProperty("message")
    public @NotNull String message;

    /**
     * Код ошибки.
     */
    @JsonProperty("code")
    public @NotNull String code;

    public ApiErrorResponseEntiry(@NotNull String message, @NotNull String code) {
        this.message = message;
        this.code = code;
    }
}
