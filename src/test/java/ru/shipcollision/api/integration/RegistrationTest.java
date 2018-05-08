package ru.shipcollision.api.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.shipcollision.api.UserTestFactory;
import ru.shipcollision.api.models.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Тест регистрации пользователей")
public class RegistrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("пользователь может зарегистрироваться")
    void testUserCanRegister() {
        final ResponseEntity<User> unauthorizedResponse = testRestTemplate.getForEntity("/me", User.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, unauthorizedResponse.getStatusCode());

        final User correctUser = UserTestFactory.createRandomUser();

        final ResponseEntity<User> userResponse =
                testRestTemplate.postForEntity(
                        "/users",
                        UserTestFactory.ProxyUser.fromUser(correctUser),
                        User.class);

        Assertions.assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());

        final User user = userResponse.getBody();

        Assertions.assertNotNull(user);
        Assertions.assertEquals(correctUser.username, user.username);
        Assertions.assertEquals(correctUser.email, user.email);
        Assertions.assertEquals(0, user.rank);

        final ResponseEntity<User> authorizedResponse = testRestTemplate.getForEntity("/me", User.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, authorizedResponse.getStatusCode());
    }
}
