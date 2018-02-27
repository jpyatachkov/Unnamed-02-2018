package ru.shipcollision.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Ответ API с описанием произошедшей ошибки.
 */
@SuppressWarnings("PublicField")
public class ApiError {

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

    public ApiError(@NotNull String message, @NotNull String code) {
        this.message = message;
        this.code = code;
    }
}
