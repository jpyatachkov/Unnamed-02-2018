package ru.shipcollision.api;

import com.github.javafaker.Faker;
import ru.shipcollision.api.models.User;

public class UserTestFactory {

    private static Faker faker = new Faker();

    public static User createRandomUser() {
        final User user = new User();
        user.username = faker.name().username();
        user.email = faker.internet().emailAddress();
        user.password = faker.internet().password();
        user.rank = (int) faker.number().randomNumber();
        user.avatarLink = faker.internet().url();
        return user;
    }

    public static User createRandomUserWithId(Long id) {
        final User user = createRandomUser();
        user.id = id;
        return user;
    }
}
