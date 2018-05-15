package ru.shipcollision.api.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.shipcollision.api.UserTestFactory;
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.services.SessionService;

import javax.servlet.http.HttpSession;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Тест контроллера текущего пользователя")
public class MeControllerTest {

    public static final String ME_ROUTE = "/me";

    @MockBean
    private SessionService sessionService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("если сессия открыта, возвращается пользователь")
    void testGetMeWithCurrentUser() {
        final User user = UserTestFactory.createRandomUserWithId((long) 0);

        Mockito.when(sessionService.getCurrentUser(Mockito.any(HttpSession.class)))
                .thenReturn(user);

        final ResponseEntity<User> response = testRestTemplate.getForEntity(ME_ROUTE, User.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("если сессия не открыта, кидается исключение")
    void testGetWithoutCurrentUser() {
        Mockito.when(sessionService.getCurrentUser(Mockito.any(HttpSession.class)))
                .thenThrow(ForbiddenException.class);

        final ResponseEntity<User> response = testRestTemplate.getForEntity(ME_ROUTE, User.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
