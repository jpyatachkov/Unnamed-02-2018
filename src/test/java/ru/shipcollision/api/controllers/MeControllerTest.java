package ru.shipcollision.api.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.shipcollision.api.CorrectUserHelper;
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.services.SessionServiceImpl;
import ru.shipcollision.api.services.UserServiceImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Тест контроллера текущего пользователя")
public class MeControllerTest {

    public static final String ME_ROUTE = "/me";

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private SessionServiceImpl sessionService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    public void mockSessionServiceWithUser() {
        Mockito.when(sessionService.getCurrentUser(Mockito.any())).thenReturn(CorrectUserHelper.getCorrectUser());
    }

    public void mockSessionServiceWithoutUser() {
        Mockito.when(sessionService.getCurrentUser(Mockito.any())).thenThrow(ForbiddenException.class);
    }

    @BeforeEach
    public void mockUserService() {
        CorrectUserHelper.mockUserService(userService);
    }

    @Test
    @DisplayName("если сессия открыта, возвращается пользователь")
    public void testGetMeWithCurrentUser() {
        mockSessionServiceWithUser();

        final ResponseEntity<User> response = testRestTemplate.getForEntity(ME_ROUTE, User.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("если сессия не открыта, кидается исключение")
    public void testGetWithoutCurrentUser() {
        mockSessionServiceWithoutUser();

        final ResponseEntity<User> response = testRestTemplate.getForEntity(ME_ROUTE, User.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("можно редактировать пользователя, если сессия открыта")
    public void testPutWithCurrentUser() {
        mockSessionServiceWithUser();

        final MeController.CreateOrFullUpdateRequest request = new MeController.CreateOrFullUpdateRequest();
        request.username = CorrectUserHelper.username;
        request.email = CorrectUserHelper.email;
        request.password = CorrectUserHelper.password;

        final ResponseEntity<User> response =
                testRestTemplate.exchange(ME_ROUTE, HttpMethod.PUT, new HttpEntity<>(request), User.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        final User user = response.getBody();

        Assertions.assertNotNull(user);
        Assertions.assertEquals(request.username, user.username);
        Assertions.assertEquals(request.email, user.email);
    }

    @Test
    @DisplayName("нельзя обновить пользователя, если сессия не открыта")
    public void testPutWithoutCurrentUser() {
        mockSessionServiceWithoutUser();

        final MeController.CreateOrFullUpdateRequest request = new MeController.CreateOrFullUpdateRequest();
        request.username = CorrectUserHelper.username;
        request.email = CorrectUserHelper.email;
        request.password = CorrectUserHelper.password;

        final ResponseEntity<User> response =
                testRestTemplate.exchange(ME_ROUTE, HttpMethod.PUT, new HttpEntity<>(request), User.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("можно выйти, если пользователь до этого вошел")
    public void testDeleteWithCurrentUser() {
        mockSessionServiceWithUser();

        final ResponseEntity<User> response =
                testRestTemplate.exchange(ME_ROUTE, HttpMethod.DELETE, null, User.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("нельзя выйти, если пользователь не вошел")
    public void testDeleteWithoutCurrentUser() {
        mockSessionServiceWithoutUser();

        final ResponseEntity<User> response =
                testRestTemplate.exchange(ME_ROUTE, HttpMethod.DELETE, null, User.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
