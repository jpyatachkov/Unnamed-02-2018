package ru.shipcollision.api;

import com.github.javafaker.Faker;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.shipcollision.api.models.User;

import java.util.Random;

@SuppressWarnings("QuestionableName")
@Component
@ConditionalOnWebApplication
public class DatabaseSeeder {

    private JdbcTemplate jdbcTemplate;

    public DatabaseSeeder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        seedUsers();
    }

    private void seedUsers() {
        // В тестовом контексте БД не должна заполняться.
        // Т.к. в тестовом контексте БД недоступна для этого компонента,
        // try-catch используется для определения контекста и прерывания операций заполнения.
        try {
            jdbcTemplate.execute("ALTER SEQUENCE users_id_seq RESTART WITH 1");
            jdbcTemplate.execute("TRUNCATE users");
        } catch (DataAccessException e) {
            return;
        }

        final Faker faker = new Faker();
        final Random random = new Random();
        final int bound = 100500;
        final String password = "password1";

        insertIntoUsers(new User(
                "a_ikchurin",
                "tyoma11.95@mail.ru",
                random.nextInt(bound),
                password
        ));
        insertIntoUsers(new User(
                "cvkucherov",
                "cvkucherov@yandex.ru",
                random.nextInt(bound),
                password
        ));
        insertIntoUsers(new User(
                "gabolaev",
                "gabolaev98@gmail.com",
                random.nextInt(bound),
                password
        ));
        insertIntoUsers(new User(
                "venger",
                "farir1408@gmail.com",
                random.nextInt(bound),
                password
        ));

        final User ao = new User(
                "a.ostapenko",
                "a.ostapenko@corp.mail.ru",
                bound,
                password
        );
        ao.avatarLink = "https://park.mail.ru/media/avatars/gtp/12/03/87f4d79e36d68c3031ccf6c55e9bbd39.jpg";
        insertIntoUsers(ao);

        final User dd = new User(
                "d.dorofeev",
                "d.dorofeev@corp.mail.ru",
                bound,
                password
        );
        dd.avatarLink = "https://park.mail.ru/media/avatars/11/17/58d4d1e7b1e97b258c9ed0b37e02d087.jpg";
        insertIntoUsers(dd);

        final User mt = new User(
                "marina.titova",
                "marina.titova@corp.mail.ru",
                bound,
                password
        );
        mt.avatarLink = "https://park.mail.ru/media/avatars/gtp/02/17/4079016d940210b4ae9ae7d41c4a2065_AQBGC0E.jpg";
        insertIntoUsers(mt);

        final User at = new User(
                "a.tyuldyukov",
                "a.tyuldyukov@corp.mail.ru",
                bound,
                password
        );
        at.avatarLink = "https://park.mail.ru/media/avatars/gtp/02/23/2e7ceec8361275c4e31fee5fe422740b.jpg";
        insertIntoUsers(at);

        for (int i = 0; i < 100; i++) {
            final String email = faker.internet().emailAddress();
            insertIntoUsers(new User(
                    email.split("@")[0],
                    email,
                    random.nextInt(bound),
                    password
            ));
        }
    }

    private void insertIntoUsers(User user) {
        jdbcTemplate.update("INSERT INTO users(username, email, rank, avatar_link, password) VALUES(?, ?, ?, ?, ?)",
                user.username, user.email, user.rank, user.avatarLink, BCrypt.hashpw(user.password, BCrypt.gensalt()));
    }
}
