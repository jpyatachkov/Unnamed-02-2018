package ru.shipcollision.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Ответ API с описанием произошедшей ошибки.
 */
public class ApiErrorResponseEntiry {

    /**
     * Описание ошибки.
     */
    @JsonProperty("message")
    private @NotNull String message;

    /**
     * Код ошибки.
     */
    @JsonProperty("code")
    private @NotNull String code;

    @SuppressWarnings("unused")
    public ApiErrorResponseEntiry(@NotNull String message, @NotNull String code) {
        this.message = message;
        this.code = code;
    }

    @SuppressWarnings("unused")
    public String getMessage() {
        return message;
    }

    @SuppressWarnings("unused")
    public void setMessage(@NotNull String message) {
        this.message = message;
    }

    @SuppressWarnings("unused")
    public String getCode() {
        return code;
    }

    @SuppressWarnings("unused")
    public void setCode(@NotNull String code) {
        this.code = code;
    }
}
