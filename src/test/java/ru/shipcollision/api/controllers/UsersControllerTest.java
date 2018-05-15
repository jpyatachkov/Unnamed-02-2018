package ru.shipcollision.api.controllers;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.shipcollision.api.UserTestFactory;
import ru.shipcollision.api.dao.UserDAO;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Тест контроллера пользователей")
public class UsersControllerTest {

    public static final String USERS_ROUTE = "/users";

    public static final String PAGE_LINK_FORMAT = "?offset=%d&limit=%d";

    @MockBean
    private UserDAO userDAO;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static List<User> generateUsersList() {
        final Faker faker = new Faker();

        final ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final User user = new User();

            user.id = (long) i;
            user.username = faker.name().username();
            user.email = faker.internet().emailAddress();
            user.rank = 10 - i;
            // Линки на аватар есть не у всех, чтобы проверить, что в случае отсутствия линка
            // такого поля в ответе сервера не будет.
            user.avatarLink = (i % 2 == 0) ? faker.internet().url() : null;
            user.password = faker.internet().password();

            users.add(user);
        }

        return users;
    }

    private static Stream<Arguments> provideUserPages() {
        final String prevPageLink = String.format(PAGE_LINK_FORMAT, 1, 10);
        final String nextPageLink = String.format(PAGE_LINK_FORMAT, 3, 10);

        return Stream.of(
                Arguments.of(generateUsersList(), prevPageLink, nextPageLink)
        );
    }

    @Test
    @DisplayName("пользователь с корректным id найден")
    public void testGetUserWithCorrectId() {
        final User user = UserTestFactory.createRandomUser();

        Mockito.when(userDAO.findById(Mockito.anyLong()))
                .thenReturn(user);

        final ResponseEntity<User> response =
                testRestTemplate.getForEntity(USERS_ROUTE + String.format("/%d", 0), User.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("при запросе пользователя с некорректным id возвращается 404")
    public void testGetUserWithincorrectId() {
        Mockito.when(userDAO.findById(Mockito.anyLong()))
                .thenThrow(NotFoundException.class);

        final ResponseEntity<User> response =
                testRestTemplate.getForEntity(USERS_ROUTE + String.format("/%d", 0), User.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("новый пользователь создается")
    public void testPostUser() {
        final User correctUser = UserTestFactory.createRandomUser();

        Mockito.when(userDAO.save(Mockito.any(User.class)))
                .thenReturn(correctUser);

        final ResponseEntity<User> response =
                testRestTemplate.postForEntity(
                        USERS_ROUTE,
                        UserTestFactory.ProxyUser.fromUser(correctUser),
                        User.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        final HttpHeaders headers = response.getHeaders();

        Assertions.assertNotNull(headers);
        Assertions.assertFalse(headers.isEmpty());

        final List<String> location = headers.get("Location");

        Assertions.assertNotNull(location);
        Assertions.assertNotNull(location.get(0));

        Assertions.assertNotNull(response.getBody());
    }
}
