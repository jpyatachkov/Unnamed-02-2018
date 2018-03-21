package ru.shipcollision.api.models;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.stream.Stream;

/**
 * Тест модели пользователя.
 */
class UserTest {

    @Test
    public void testIdIncrements() {
        final User u1 = new User();
        final User u2 = new User();

        Assertions.assertNotEquals(u1.id, u2.id);
        Assertions.assertTrue(u1.id < u2.id, "Second user id must be > than first user id");
    }

    @ParameterizedTest
    @MethodSource("provideCorrectUserData")
    public void testUserCreatedWithCorrectData(String username, String email, int rank, String password) {
        User user = new User(username, email, password);
        Assertions.assertNotNull(user);
        Assertions.assertNull(user.avatarLink);
        Assertions.assertTrue(user.id >= 0, "User id must be >= 0");
        Assertions.assertEquals(username, user.username);
        Assertions.assertEquals(email, user.email);
        Assertions.assertEquals(password, user.password);
        Assertions.assertNotEquals(rank, user.rank);

        user = new User(username, email, rank, password);
        Assertions.assertNull(user.avatarLink);
        Assertions.assertTrue(user.id >= 0, "User id must be >= 0");
        Assertions.assertEquals(username, user.username);
        Assertions.assertEquals(email, user.email);
        Assertions.assertEquals(password, user.password);
        Assertions.assertEquals(rank, user.rank);
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectUserData")
    public void testUserNotCreatedWithInvalidData(String username, String email, int rank, String password) {
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        final User user = new User(username, email, rank, password);

        Assertions.assertFalse(validatorFactory.getValidator().validate(user).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("provideObjectPairs")
    public void testEquals(@NotNull Object user1, Object user2, boolean equals) {
        Assertions.assertEquals(user1.equals(user2), equals);
    }

    @Test
    public void testHashCode() {
        final User user = new User();
        Assertions.assertEquals(user.id.intValue(), user.hashCode());
    }

    private static Stream<Arguments> provideCorrectUserData() {
        return Stream.of(
                Arguments.of("username", "email@mail.ru", 1, "password1")
        );
    }

    private static Stream<Arguments> provideIncorrectUserData() {
        final Faker faker = new Faker();

        final String correctUsername = faker.name().username();
        final String correctEmail = faker.internet().emailAddress();
        final int correctRank = 100500;
        final String correctPassword = faker.internet().password();

        return Stream.of(
                Arguments.of(null, correctEmail, correctRank, correctPassword),
                Arguments.of("", correctEmail, correctRank, correctPassword),
                Arguments.of("aaa", correctEmail, correctRank, correctPassword),
                Arguments.of(correctUsername, null, correctRank, correctPassword),
                Arguments.of(correctUsername, "", correctRank, correctPassword),
                Arguments.of(correctUsername, "not_email", correctRank, correctPassword),
                Arguments.of(correctUsername, correctEmail, -10, correctPassword),
                Arguments.of(correctUsername, correctEmail, correctRank, null),
                Arguments.of(correctUsername, correctEmail, correctRank, ""),
                Arguments.of(correctUsername, correctEmail, correctRank, "aaa")
        );
    }

    private static Stream<Arguments> provideObjectPairs() {
        final User user = new User();

        return Stream.of(
                Arguments.of(new User(), null, false),
                Arguments.of(new User(), "aaaa", false),
                Arguments.of(new User(), new User(), false),
                Arguments.of(
                        new User("username", "email@mail.ru", "password1"),
                        new User("username", "email@mail.ru", "password1"),
                        false
                ),
                Arguments.of(user, user, true)
        );
    }
}
