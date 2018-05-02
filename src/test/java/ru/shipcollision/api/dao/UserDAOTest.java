package ru.shipcollision.api.dao;

import com.github.javafaker.Faker;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.google.common.collect.Ordering;
import ru.shipcollision.api.controllers.MeController;
import ru.shipcollision.api.exceptions.InvalidCredentialsException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;

import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Тест DAO пользователей")
class UserDAOTest {

    @SuppressWarnings("resource")
    @Rule
    private static PostgreSQLContainer postgres = new PostgreSQLContainer();

    private static User correctUser;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDAO userDAO;

    private static User getRandomUser() {
        final Faker faker = new Faker();

        final User user = new User();
        user.username = faker.name().username();
        user.email = faker.internet().emailAddress();
        user.rank = (int) faker.number().randomNumber();
        user.avatarLink = faker.internet().url();
        user.password = faker.internet().url();

        return user;
    }

    @BeforeAll
    static void setupCorrectUser() {
        correctUser = getRandomUser();
        correctUser.id = (long) 0;
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
        for (int i = 0; i < 5; i++) {
            insertIntoUsers(getRandomUser());
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
        final User correctUserCandidate = getRandomUser();
        Assertions.assertNotEquals(correctUser, correctUserCandidate);
        Assertions.assertThrows(
                InvalidCredentialsException.class,
                () -> userDAO.authenticate(correctUser.email, correctUserCandidate.password)
        );
    }

    @Test
    @DisplayName("сортировка пользователей по рейтингу работает корректно")
    void testGetByRating() {
        final List<Integer> acsRanks = userDAO.getByRating(true)
                .stream()
                .map(el -> el.rank)
                .collect(Collectors.toList());
        final List<Integer> descRanks = userDAO.getByRating(false)
                .stream()
                .map(el -> el.rank)
                .collect(Collectors.toList());

        Assertions.assertNotEquals(1, acsRanks.size());
        Assertions.assertNotEquals(1, descRanks.size());
        Assertions.assertTrue(Ordering.natural().isOrdered(acsRanks));
        Assertions.assertTrue(Ordering.natural().reverse().isOrdered(descRanks));
    }

    @Test
    @DisplayName("создание ранее не существовавшего пользователя работает корректно")
    void testCreateUserValidCredentials() {
        final User user = getRandomUser();

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
        final User user = getRandomUser();

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
        final User user = getRandomUser();
        user.email = correctUser.email;

        Assertions.assertThrows(
                InvalidCredentialsException.class,
                () -> userDAO.save(user)
        );
    }

    @Test
    @DisplayName("частичное обновление пользователя работает корректно")
    void testPartialUpdate() {
        final User user = getRandomUser();
        User saved = userDAO.save(user);

        final Faker faker = new Faker();

        final MeController.PartialUpdateRequest partialUpdateRequest = new MeController.PartialUpdateRequest();
        partialUpdateRequest.username = faker.name().username();
        partialUpdateRequest.email = faker.internet().emailAddress();
        partialUpdateRequest.password = faker.internet().password();

        saved = userDAO.partialUpdate(saved.id, partialUpdateRequest);

        final User foundAfterSave = jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE email = ?",
                new Object[]{saved.email},
                UserDAO.USER_ROW_MAPPER);

        Assertions.assertEquals(saved, foundAfterSave);
        Assertions.assertTrue(BCrypt.checkpw(partialUpdateRequest.password, saved.password));
    }
}
