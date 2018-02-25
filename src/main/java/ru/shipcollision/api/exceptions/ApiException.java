package ru.shipcollision.api.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;

/**
 * Базовый класс иключений, которые должны приводить к демонстрации сообщений НЕ с 5ХХ-статусом.
 */
public class ApiException extends Exception {

    /**
     * Сообщение, которое будет показано клиенту.
     */
    private ExceptionMessage exceptionMessage;

    /**
     * HTTP-статус ответа.
     */
    private HttpStatus httpStatus;

    public ApiException() {
        super();
        this.exceptionMessage = new ExceptionMessage(getDefaultErrorMessage(), getDefaultErrorCode());
        this.httpStatus = getDefaultHttpStatus();
    }

    public ApiException(@NotNull String errorMessage) {
        super();
        this.exceptionMessage = new ExceptionMessage(errorMessage, getDefaultErrorCode());
        this.httpStatus = getDefaultHttpStatus();
    }

    public ApiException(Exception exception) {
        super(exception);
        this.exceptionMessage = new ExceptionMessage(exception.getMessage(), getDefaultErrorCode());
        this.httpStatus = getDefaultHttpStatus();
    }

    protected String getDefaultErrorMessage() {
        return "Error occured";
    }

    protected String getDefaultErrorCode() {
        return "error";
    }

    protected HttpStatus getDefaultHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    public ExceptionMessage getExceptionMessage() {
        return exceptionMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    /**
     * Класс для инкапсуляции сообщения пользователю.
     * Вынесено в отдельный класс, так как сериализация Exception с кастомными полями представляется
     * слишком костыльной.
     */
    public static class ExceptionMessage {

        @JsonProperty("message")
        private String errorMessage;

        @JsonProperty("code")
        private String errorCode;

        public ExceptionMessage(String errorMessage, String errorCode) {
            this.errorMessage = errorMessage;
            this.errorCode = errorCode;
        }

        @SuppressWarnings("unused")
        public String getErrorMessage() {
            return errorMessage;
        }

        @SuppressWarnings("unused")
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @SuppressWarnings("unused")
        public String getErrorCode() {
            return errorCode;
        }

        @SuppressWarnings("unused")
        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }
    }
}
