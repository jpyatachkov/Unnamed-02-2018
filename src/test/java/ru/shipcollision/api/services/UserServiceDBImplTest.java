package ru.shipcollision.api.services;

import com.github.javafaker.Faker;
import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.shipcollision.api.models.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = UserServiceDBImpl.class
)
@DisplayName("Тест контроллера пользователей")
public class UserServiceDBImplTest {

    @Autowired
    private UserServiceDBImpl userServiceDB;

    private Faker faker;

    @BeforeEach
    public void setFaker() {
        faker = new Faker();
    }

    @Test
    @Description("проверяет, что пользователь сохраняется в БД корректно")
    public void testUserCanBeSaved() {
        User user = new User(faker.name().username(),
                faker.internet().emailAddress(),
                0,
                faker.internet().password());

        userServiceDB.save(user);
    }
}
