package ru.shipcollision.api.services;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.shipcollision.api.exceptions.InvalidParamsException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;

import java.util.List;
import java.util.stream.Stream;

/**
 * Тест сервиса пользователей.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = UserServiceImpl.class
)
class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    /**
     * Проверяеть сортировку пользователей по рейтингу.
     */
    @Test
    public void testGetByRating() {
        final List<User> usersByRating = userService.getByRating(false);

        for (int i = 0; i < usersByRating.size() - 1; i += 2) {
            Assertions.assertTrue(
                    usersByRating.get(i).rank >= usersByRating.get(i + 1).rank,
                    "Rank of second user is greater then rank of first"
            );
        }
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectIds")
    public void testCannotFindUserByIncorrectIds(long incorrectId) {
        Assertions.assertThrows(NotFoundException.class, () -> userService.findById(incorrectId));
    }

    @ParameterizedTest
    @MethodSource("provideCorrectEmails")
    public void testCanFindUserByCorrectEmail(String correctEmail) {
        Assertions.assertNotNull(userService.findByEmail(correctEmail));
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectEmails")
    public void testCannotFindUserByIncorrectEmail(String incorrectEmail) {
        Assertions.assertThrows(NotFoundException.class, () -> userService.findByEmail(incorrectEmail));
    }

    @ParameterizedTest
    @MethodSource("provideUsersWithCorrectFields")
    public void testCanSaveUsersWithCorrectFields(User user) {
        userService.save(user);
        Assertions.assertEquals(user, userService.findByEmail(user.email));
    }

    @ParameterizedTest
    @MethodSource("provideUsersWithIncorrectFields")
    public void testCannotSaveUsersWithIncorrectFields(User user) {
        Assertions.assertThrows(InvalidParamsException.class, () -> userService.save(user));
    }

    @Test
    public void canDeleteExisting() {
        final User user = new User("a", "a@a.com", "p");
        userService.save(user);
        Assertions.assertTrue(userService.hasUser(user));
        userService.delete(user);
        Assertions.assertFalse(userService.hasUser(user));
    }

    private static Stream<Arguments> provideIncorrectIds() {
        return Stream.of(
                Arguments.of((long) -1)
        );
    }

    private static Stream<Arguments> provideCorrectEmails() {
        return Stream.of(
                Arguments.of("gabolaev98@gmail.com"),
                Arguments.of("a.ostapenko@corp.mail.ru")
        );
    }

    private static Stream<Arguments> provideIncorrectEmails() {
        return Stream.of(
                Arguments.of("aaaa"),
                Arguments.of("not-existing-email@corp.mail.ru")
        );
    }

    private static Stream<Arguments> provideUsersWithCorrectFields() {
        return Stream.of(
                Arguments.of(new User("newnickname", "new@email.com", "password1")),
                Arguments.of(new User("evenmorenewnickname", "even.more.new@email.com", "password1"))
        );
    }

    private static Stream<Arguments> provideUsersWithIncorrectFields() {
        return Stream.of(
                Arguments.of(new User("", "email@mail.ru", "password1")),
                Arguments.of(new User("username", "", "password1")),
                Arguments.of(new User("username", "email@mail.ru", "")),
                Arguments.of(new User("cvkucherov", "email@mail.ru", "password1")),
                Arguments.of(new User("cvk", "cvkucherov@yandex.ru", "password1")),
                Arguments.of(new User("cvkucherov", "cvkucherov@yandex.ru", "password1"))
        );
    }
}
