package ru.shipcollision.api.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.constraints.NotNull;
import java.util.stream.Stream;

/**
 * Тест модели пользователя.
 */
class UserTest {

    private static Stream<Arguments> provideCorrectUserData() {
        return Stream.of(
                Arguments.of(new UserData("username", "email@mail.ru", 1, "password1"))
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

    /**
     * Проверяет, что ID увеличивается.
     */
    @Test
    public void testIdIncrements() {
        final User u1 = new User();
        final User u2 = new User();

        Assertions.assertNotEquals(u1.id, u2.id);
        Assertions.assertTrue(u1.id < u2.id, "Second user id must be > than first user id");
    }

    /**
     * Проверяет, что пользователь может быть создан с валидными данными
     * (проверка включает использование обоих конструкторов).
     *
     * @param data Данные пользователя.
     */
    @ParameterizedTest
    @MethodSource("provideCorrectUserData")
    public void testCanCreateUserWithValidData(UserData data) {
        User user = new User(data.username, data.email, data.password);
        Assertions.assertNull(user.avatarLink);
        Assertions.assertTrue(user.id >= 0, "User id must be >= 0");
        Assertions.assertEquals(data.username, user.username);
        Assertions.assertEquals(data.email, user.email);
        Assertions.assertEquals(data.password, user.password);
        Assertions.assertNotEquals(data.rank, user.rank);

        user = new User(data.username, data.email, data.rank, data.password);
        Assertions.assertNull(user.avatarLink);
        Assertions.assertTrue(user.id >= 0, "User id must be >= 0");
        Assertions.assertEquals(data.username, user.username);
        Assertions.assertEquals(data.email, user.email);
        Assertions.assertEquals(data.password, user.password);
        Assertions.assertEquals(data.rank, user.rank);
    }

    /**
     * Проверяет работу метода equals.
     * Только объекты с одинаковым id равны независимо от содержания.
     *
     * @param user1
     * @param user2
     * @param equals
     */
    @SuppressWarnings("JavaDoc")
    @ParameterizedTest
    @MethodSource("provideObjectPairs")
    public void testEquals(@NotNull Object user1, Object user2, boolean equals) {
        Assertions.assertEquals(user1.equals(user2), equals);
    }

    /**
     * Проверяет работу метода hashCode.
     * Должен возвращать id.
     */
    @Test
    public void testHashCode() {
        final User user = new User();
        Assertions.assertEquals(user.id.intValue(), user.hashCode());
    }

    @SuppressWarnings("PublicField")
    private static class UserData {

        public String username;

        public String email;

        public int rank;

        public String password;

        UserData(String username, String email, int rank, String password) {
            this.username = username;
            this.email = email;
            this.rank = rank;
            this.password = password;
        }
    }
}
