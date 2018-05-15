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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.shipcollision.api.UserTestFactory;
import ru.shipcollision.api.dao.UserDAO;
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.services.SessionService;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Тест контроллера сессий пользователя.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Тест конктроллера сессий")
public class SessionsControllerTest {

    public static final String SIGNIN_ROUTE = "/signin";

    public static final String SIGNOUT_ROUTE = "/signout";

    @MockBean
    private SessionService sessionService;

    @MockBean
    private UserDAO userDAO;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("можно войти из-под корректного аккаунта")
    public void testCorrectSignin() {
        final User user = UserTestFactory.createRandomUserWithId((long) 0);

        Mockito.when(userDAO.authenticate(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(user);
        Mockito.doCallRealMethod()
                .when(sessionService)
                .openSession(Mockito.any(HttpSession.class), Mockito.any(User.class));

        final SessionsController.SigninRequest signinRequest =
                new SessionsController.SigninRequest(user.email, user.password);
        final ResponseEntity<User> response = testRestTemplate.postForEntity(SIGNIN_ROUTE, signinRequest, User.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        final HttpHeaders responseHeaders = response.getHeaders();
        final User responseUser = response.getBody();

        Assertions.assertNotNull(responseHeaders);
        final List<String> cookies = responseHeaders.get("Set-Cookie");
        Assertions.assertNotNull(cookies);
        Assertions.assertFalse(cookies.isEmpty());

        Assertions.assertNotNull(responseUser);
        Assertions.assertEquals(user.email, responseUser.email);
        Assertions.assertEquals(user.username, responseUser.username);
        Assertions.assertNull(responseUser.password);
    }

    @Test
    @DisplayName("нельзя войти, если логин и/или пароль не совпадает с корректным")
    public void testIncorrectSignin() {
        Mockito.when(userDAO.authenticate(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(NotFoundException.class);

        final SessionsController.SigninRequest request =
                new SessionsController.SigninRequest("anyEmail", "anyPassword");
        final ResponseEntity<Object> response = testRestTemplate.postForEntity(SIGNIN_ROUTE, request, Object.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("можно выйти, если был осуществлен вход")
    public void testCorrectSignout() {
        final User user = UserTestFactory.createRandomUserWithId((long) 0);

        Mockito.when(sessionService.getCurrentUser(Mockito.any(HttpSession.class)))
                .thenReturn(user);

        final ResponseEntity<Object> response =
                testRestTemplate.exchange(SIGNOUT_ROUTE, HttpMethod.DELETE, null, Object.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("403 при попытке выхода, если сессии нет в куках")
    public void testNoUserSignout() {
        Mockito.when(sessionService.getCurrentUser(Mockito.any(HttpSession.class)))
                .thenThrow(ForbiddenException.class);

        final ResponseEntity<Object> response =
                testRestTemplate.exchange(SIGNOUT_ROUTE, HttpMethod.DELETE, null, Object.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
