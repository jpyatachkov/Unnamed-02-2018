package ru.shipcollision.api.services;

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
    @MethodSource("provideCorrectIds")
    public void testCanFindUserByCorrectIds(long correctId) {
        Assertions.assertNotNull(userService.findById(correctId));
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

    @Test
    public void testCanSaveNotExisting() {
        final User existingUser = userService.findById((long) 1);
        Assertions.assertNotNull(existingUser);

        final int newRank = existingUser.rank + 200;
        existingUser.rank = newRank;
        userService.save(existingUser);
        final User savedUser = userService.findById((long) 1);
        Assertions.assertEquals(existingUser, savedUser);
        Assertions.assertEquals(newRank, savedUser.rank);
    }

    @Test
    public void testCanNotSaveExisting() {
        final User user = userService.findById((long) 1);
        user.id = Long.MAX_VALUE;
        Assertions.assertThrows(InvalidParamsException.class, () -> userService.save(user));
    }

    private static Stream<Arguments> provideCorrectIds() {
        return Stream.of(
                Arguments.of((long) 1),
                Arguments.of((long) 0),
                Arguments.of((long) 100)
        );
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
}
