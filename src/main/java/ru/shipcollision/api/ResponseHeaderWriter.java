package ru.shipcollision.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Класс для добавления CORS-заголовков к любому ответу сервера.
 */
@ControllerAdvice
public class ResponseHeaderWriter implements ResponseBodyAdvice<Object> {

    @Value("${ru.shipcollision.api.access-control-allow-origin}")
    private String accessControlAllowOrigin;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        response.getHeaders().add("Access-Control-Allow-Methods", "DELETE, PATCH, POST, PUT");
        response.getHeaders().add("Access-Control-Allow-Credentials", "true");
        response.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, *");
        response.getHeaders().add("Access-Control-Allow-Origin", accessControlAllowOrigin);
        return body;
    }
}
