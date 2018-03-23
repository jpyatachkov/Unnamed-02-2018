package ru.shipcollision.api.controllers;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.AdditionalMatchers;
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
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.exceptions.InvalidCredentialsException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.services.SessionServiceImpl;
import ru.shipcollision.api.services.UserServiceImpl;

import java.util.List;
import java.util.stream.Stream;

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
    private SessionServiceImpl sessionService;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private User getCorrectUser() {
        final User correctUser = new User();
        correctUser.id = CorrectUserParams.id;
        correctUser.username = CorrectUserParams.username;
        correctUser.email = CorrectUserParams.email;
        correctUser.rank = CorrectUserParams.rank;
        correctUser.password = CorrectUserParams.password;
        return correctUser;
    }

    /**
     * Эмуляция залогиненного пользователя.
     */
    private void mockSessionServiceWithUser() {
        final User correctUser = getCorrectUser();
        Mockito.when(sessionService.getCurrentUser(Mockito.any())).thenReturn(correctUser);
    }

    /**
     * Эмуляция незалогиненного пользователя.
     */
    private void mockSessionServiceWithoutUser() {
        Mockito.when(sessionService.getCurrentUser(Mockito.any())).thenThrow(ForbiddenException.class);
    }

    @BeforeEach
    public void mockUserService() {
        final User correctUser = getCorrectUser();

        // UserService будет отвечать успешно, только если кидать ему на вход
        // correctUser'a или его атрибуты. В остальных случаях будет исключение.
        Mockito.when(userService.hasEmail(correctUser.email))
                .thenReturn(true);
        Mockito.when(userService.hasEmail(AdditionalMatchers.not(Mockito.eq(correctUser.email))))
                .thenThrow(InvalidCredentialsException.class);

        Mockito.when(userService.hasId(correctUser.id))
                .thenReturn(true);
        Mockito.when(userService.hasId(AdditionalMatchers.not(Mockito.eq(correctUser.id))))
                .thenThrow(InvalidCredentialsException.class);

        Mockito.when(userService.hasUser(correctUser))
                .thenReturn(true);
        Mockito.when(userService.hasUser(AdditionalMatchers.not(Mockito.eq(correctUser))))
                .thenThrow(InvalidCredentialsException.class);

        Mockito.when(userService.hasusername(correctUser.username))
                .thenReturn(true);
        Mockito.when(userService.hasusername(AdditionalMatchers.not(Mockito.eq(correctUser.username))))
                .thenThrow(InvalidCredentialsException.class);

        Mockito.when(userService.findById(correctUser.id))
                .thenReturn(correctUser);
        Mockito.when(userService.findById(AdditionalMatchers.not(Mockito.eq(correctUser.id))))
                .thenThrow(NotFoundException.class);

        Mockito.when(userService.findByEmail(correctUser.email))
                .thenReturn(correctUser);
        Mockito.when(userService.findByEmail(AdditionalMatchers.not(Mockito.eq(correctUser.email))))
                .thenThrow(NotFoundException.class);
    }

    @Test
    @DisplayName("можно войти из-под корректного аккаунта")
    public void testCorrectSignin() {
        mockSessionServiceWithUser();

        final User user = getCorrectUser();
        final SessionsController.SigninRequest signinRequest =
                new SessionsController.SigninRequest(user.email, user.password);
        final ResponseEntity<User> response = testRestTemplate.postForEntity(SIGNIN_ROUTE, signinRequest, User.class);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

        final HttpHeaders responseHeaders = response.getHeaders();
        final User responseUser = response.getBody();

        Assertions.assertNotNull(responseHeaders);
        final List<String> cookies = responseHeaders.get("Set-Cookie");
        Assertions.assertNotNull(cookies);
        Assertions.assertFalse(cookies.isEmpty());

        Assertions.assertNotNull(responseUser);
        Assertions.assertEquals(responseUser.email, user.email);
        Assertions.assertEquals(responseUser.username, user.username);
        Assertions.assertNull(responseUser.password);
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectCredentials")
    @DisplayName("нельзя войти, если логин и/или пароль не совпадает с корректным")
    public void testIncorrectSignin(String email, String password) {
        mockSessionServiceWithUser();

        final SessionsController.SigninRequest request =
                new SessionsController.SigninRequest(email, password);
        final ResponseEntity<Object> response = testRestTemplate.postForEntity(SIGNIN_ROUTE, request, Object.class);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("можно выйти, если был осуществлен вход")
    public void testCorrectSignout() {
        mockSessionServiceWithUser();
        final ResponseEntity<Object> response =
                testRestTemplate.exchange(SIGNOUT_ROUTE, HttpMethod.DELETE, null, Object.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @DisplayName("403 при попытке выхода, если сессии нет в куках")
    public void testNoUserSignout() {
        mockSessionServiceWithoutUser();
        final ResponseEntity<Object> response =
                testRestTemplate.exchange(SIGNOUT_ROUTE, HttpMethod.DELETE, null, Object.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    private static Stream<Arguments> provideIncorrectCredentials() {
        final Faker faker = new Faker();

        return Stream.of(
                Arguments.of(CorrectUserParams.email, faker.internet().password()),
                Arguments.of(faker.internet().emailAddress(), CorrectUserParams.password),
                Arguments.of(faker.internet().emailAddress(), faker.internet().password())
        );
    }

    @SuppressWarnings("PublicField")
    private static class CorrectUserParams {

        public static Long id;

        public static String username;

        public static String email;

        public static int rank;

        public static String password;

        static {
            final Faker faker = new Faker();

            id = (long) 1;
            username = faker.name().username();
            email = faker.internet().emailAddress();
            rank = 1;
            password = faker.internet().password();
        }
    }
}
