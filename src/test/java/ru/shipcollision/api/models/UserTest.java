package ru.shipcollision.api.models;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.stream.Stream;

@DisplayName("Тест модели пользователя")
class UserTest {

    private static Stream<Arguments> provideCorrectUserData() {
        final Faker faker = new Faker();

        return Stream.of(
                Arguments.of(faker.name().username(), faker.internet().emailAddress(), 1, faker.internet().password())
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
                Arguments.of(new User(), new User(), true),
                Arguments.of(
                        new User("username", "email@mail.ru", "password1"),
                        new User("username", "email@mail.ru", "password1"),
                        true
                ),
                Arguments.of(user, user, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCorrectUserData")
    @DisplayName("можно создать пользователя с корректными данными")
    void testUserCreatedWithCorrectData(String username, String email, int rank, String password) {
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        final Validator validator = validatorFactory.getValidator();

        User user = new User(username, email, password);

        Assertions.assertTrue(validator.validate(user).isEmpty());

        Assertions.assertNotNull(user);
        Assertions.assertNull(user.avatarLink);
        Assertions.assertNull(user.id);
        Assertions.assertEquals(username, user.username);
        Assertions.assertEquals(email, user.email);
        Assertions.assertEquals(password, user.password);
        Assertions.assertNotEquals(rank, user.rank);

        user = new User(username, email, rank, password);

        Assertions.assertTrue(validator.validate(user).isEmpty());

        Assertions.assertNull(user.avatarLink);
        Assertions.assertNull(user.id);
        Assertions.assertEquals(username, user.username);
        Assertions.assertEquals(email, user.email);
        Assertions.assertEquals(password, user.password);
        Assertions.assertEquals(rank, user.rank);
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectUserData")
    @DisplayName("валидации пользователя с некорректными данными не проходят")
    void testUserNotCreatedWithInvalidData(String username, String email, int rank, String password) {
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        final User user = new User(username, email, rank, password);
        Assertions.assertFalse(validatorFactory.getValidator().validate(user).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("provideObjectPairs")
    @DisplayName("проверка объектов на равенство дает корректные результаты")
    void testEquals(@NotNull Object user1, Object user2, boolean equals) {
        Assertions.assertEquals(equals, user1.equals(user2));
    }

    @Test
    @DisplayName("сравнение учитывает изменения в объектах")
    void testMutableEquals() {
        final User user1 = new User("username", "email@mail.ru", "password1");

        Assertions.assertEquals(user1, user1);

        final User user2 = new User(user1);
        user2.email = "lol@mail.ru";

        Assertions.assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("хэш-коды равных объектов равны")
    void testHashCode() {
        final User user1 = new User("username", "email@mail.ru", "password1");
        final User user2 = new User("username", "email@mail.ru", "password1");

        Assertions.assertEquals(user1, user2);
        Assertions.assertEquals(user1.hashCode(), user2.hashCode());
    }
}
