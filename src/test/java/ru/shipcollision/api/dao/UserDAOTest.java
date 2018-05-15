package ru.shipcollision.api.dao;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.shaded.com.google.common.collect.Ordering;
import ru.shipcollision.api.UserTestFactory;
import ru.shipcollision.api.exceptions.InvalidCredentialsException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.exceptions.PaginationException;
import ru.shipcollision.api.models.User;

import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("Тест DAO пользователей")
class UserDAOTest {

    public static final int FAKE_USERS_COUNT = 5;

    private static User correctUser;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDAO userDAO;

    @BeforeAll
    static void setupCorrectUser() {
        correctUser = UserTestFactory.createRandomUserWithId((long) 0);
    }

    private void insertIntoUsers(User user) {
        jdbcTemplate.update("INSERT INTO users(username, email, rank, avatar_link, password) " +
                        "VALUES(?, ?, ?, ?, ?)",
                user.username, user.email, user.rank, user.avatarLink, BCrypt.hashpw(user.password, BCrypt.gensalt()));
    }

    @BeforeEach
    void setupDatabase() {
        jdbcTemplate.update("INSERT INTO users(id, username, email, rank, avatar_link, password) " +
                        "VALUES(?, ?, ?, ?, ?, ?)",
                correctUser.id, correctUser.username, correctUser.email, correctUser.rank, correctUser.avatarLink,
                BCrypt.hashpw(correctUser.password, BCrypt.gensalt()));

        // Для скорборда.
        for (int i = 0; i < FAKE_USERS_COUNT; i++) {
            insertIntoUsers(UserTestFactory.createRandomUser());
        }
    }

    @AfterEach
    void teardownDatabase() {
        jdbcTemplate.execute("TRUNCATE users");
    }

    @Test
    @DisplayName("существующий пользователь может быть найден по id")
    void testCanFindExistingUserById() {
        Assertions.assertEquals(correctUser, userDAO.findById(correctUser.id));
    }

    @Test
    @DisplayName("при поиске несуществующего пользователя по id выбрасывается исключение")
    void cannotFindNotExistingUserById() {
        Assertions.assertThrows(NotFoundException.class, () -> userDAO.findById((long) -1));
    }

    @Test
    @DisplayName("существующий пользователь может быть найден по электронной почте")
    void testCanFindExistingUserByEmail() {
        Assertions.assertEquals(correctUser, userDAO.findByEmail(correctUser.email));
    }

    @Test
    @DisplayName("при поиске несуществующего пользователя по электронной почте выбрасывается исключение")
    void testCannotFindNotExistingUserByEmail() {
        Assertions.assertThrows(NotFoundException.class, () -> userDAO.findByEmail("email@mail.ru"));
    }

    @Test
    @DisplayName("пользователь с правильным логином и паролем может войти")
    void testAuthenticateValidCredentials() {
        final User correctUserCandidate = correctUser;
        Assertions.assertEquals(
                correctUser,
                userDAO.authenticate(correctUserCandidate.email, correctUserCandidate.password)
        );
    }

    @Test
    @DisplayName("пользователь с неверным логином и паролем не будет авторизован")
    void testAuthenticateInvalidCredentials() {
        final User correctUserCandidate = UserTestFactory.createRandomUser();
        Assertions.assertNotEquals(correctUser, correctUserCandidate);
        Assertions.assertThrows(
                InvalidCredentialsException.class,
                () -> userDAO.authenticate(correctUser.email, correctUserCandidate.password)
        );
    }

    @Test
    @DisplayName("сортировка пользователей по рейтингу работает корректно")
    void testGetByRating() {
        final int defaultOffset = 0;
        final int defaultLimit = 10;

        final List<Integer> acsRanks = userDAO.getByRating(true, defaultOffset, defaultLimit)
                .stream()
                .map(el -> el.rank)
                .collect(Collectors.toList());
        final List<Integer> descRanks = userDAO.getByRating(false, defaultOffset, defaultLimit)
                .stream()
                .map(el -> el.rank)
                .collect(Collectors.toList());

        // +1 из-за "корректного" юзера, который добавляется к созданным FAKE_USERS_COUNT юзерам.
        @SuppressWarnings("ConstantConditions") final int expectedSize = (FAKE_USERS_COUNT + 1 > defaultLimit) ? defaultLimit : FAKE_USERS_COUNT + 1;

        Assertions.assertEquals(expectedSize, acsRanks.size());
        Assertions.assertEquals(expectedSize, descRanks.size());
        Assertions.assertTrue(Ordering.natural().isOrdered(acsRanks));
        Assertions.assertTrue(Ordering.natural().reverse().isOrdered(descRanks));
    }

    @Test
    @DisplayName("пагинация не работает с неверными значениями offset и limit")
    void testGetByRatingWontWork() {
        Assertions.assertThrows(PaginationException.class, () -> userDAO.getByRating(true, 10, -1));
        Assertions.assertThrows(PaginationException.class, () -> userDAO.getByRating(true, -10, 1));
        Assertions.assertThrows(PaginationException.class, () -> userDAO.getByRating(true, -10, -1));
    }

    @Test
    @DisplayName("создание ранее не существовавшего пользователя работает корректно")
    void testCreateUserValidCredentials() {
        final User user = UserTestFactory.createRandomUser();

        final List<User> foundBeforeSave = jdbcTemplate.query(
                "SELECT * FROM users WHERE email = ?",
                new Object[]{user.email},
                UserDAO.USER_ROW_MAPPER);
        Assertions.assertEquals(0, foundBeforeSave.size());

        final User saved = userDAO.save(user);

        final List<User> foundAfterSave = jdbcTemplate.query(
                "SELECT * FROM users WHERE email = ?",
                new Object[]{user.email},
                UserDAO.USER_ROW_MAPPER);

        Assertions.assertNotNull(foundAfterSave);
        Assertions.assertEquals(1, foundAfterSave.size());
        Assertions.assertEquals(saved, foundAfterSave.get(0));
    }

    @Test
    @DisplayName("при создании ранее не существовавшего пользователя учитывается переданный id")
    void testCreateUserValidCredentialsAndId() {
        final User user = UserTestFactory.createRandomUser();

        final List<User> foundBeforeSave = jdbcTemplate.query(
                "SELECT * FROM users WHERE email = ?",
                new Object[]{user.email},
                UserDAO.USER_ROW_MAPPER);
        Assertions.assertEquals(0, foundBeforeSave.size());

        user.id = (long) 100;
        final User saved = userDAO.save(user);

        Assertions.assertEquals(Long.valueOf(100), saved.id);

        final List<User> foundAfterSave = jdbcTemplate.query(
                "SELECT * FROM users WHERE email = ?",
                new Object[]{user.email},
                UserDAO.USER_ROW_MAPPER);

        Assertions.assertNotNull(foundAfterSave);
        Assertions.assertEquals(1, foundAfterSave.size());
        Assertions.assertEquals(saved, foundAfterSave.get(0));
    }

    @Test
    @DisplayName("при попытке создания пользователя с неверными полями выбрасывается ошибка")
    void testCreateUserInvalidCredentials() {
        final User user = UserTestFactory.createRandomUser();
        user.email = correctUser.email;

        Assertions.assertThrows(
                InvalidCredentialsException.class,
                () -> userDAO.save(user)
        );
    }

    @Test
    @DisplayName("обновление пользователя без пароля работает корректно")
    void testPartialUpdate() {
        final User user = UserTestFactory.createRandomUser();
        User saved = userDAO.save(user);

        final Faker faker = new Faker();

        saved.username = faker.name().username();
        saved.email = faker.internet().emailAddress();
        saved.password = faker.internet().password();

        saved = userDAO.update(saved);

        final User foundAfterSave = jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE email = ?",
                new Object[]{saved.email},
                UserDAO.USER_ROW_MAPPER);

        Assertions.assertEquals(saved, foundAfterSave);
    }

    @Test
    @DisplayName("обновление пароля работает корректно")
    void testUpdatePassword() {
        final User user = UserTestFactory.createRandomUser();
        User saved = userDAO.save(user);

        final String newPassword = "newPassword";
        Assertions.assertFalse(BCrypt.checkpw(newPassword, saved.password));

        saved.password = newPassword;
        saved = userDAO.updatePassword(saved);

        Assertions.assertTrue(BCrypt.checkpw(newPassword, saved.password));
    }

    @Test
    @DisplayName("метод удаления аватара пользователя работает корректно")
    void testRemoveAvatar() {
        User user = UserTestFactory.createRandomUser();
        insertIntoUsers(user);

        Assertions.assertNotNull(user.avatarLink);
        user = userDAO.removeAvatar(user);
        Assertions.assertNotNull(user);
    }
}
