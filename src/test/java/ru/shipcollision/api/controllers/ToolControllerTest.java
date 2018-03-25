package ru.shipcollision.api.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Тест контроллера проверки доступности API")
public class ToolControllerTest {

    public static final String PING_ROUTE = "/ping";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("API доступно")
    public void testAPIAccessible() {
        final ResponseEntity<Object> response =
                testRestTemplate.exchange(PING_ROUTE, HttpMethod.HEAD, null, Object.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        final HttpHeaders headers = response.getHeaders();
        Assertions.assertFalse(headers.isEmpty());
        Assertions.assertFalse(headers.get("Status").isEmpty());
        Assertions.assertEquals("OK", headers.get("Status").get(0));
    }
}
