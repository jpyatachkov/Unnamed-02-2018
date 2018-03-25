package ru.shipcollision.api.services;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;
import ru.shipcollision.api.controllers.MeController;
import ru.shipcollision.api.exceptions.InvalidParamsException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, User> allUsers;

    @SuppressWarnings("QuestionableName")
    public UserServiceImpl() {
        final Faker faker = new Faker();
        final Random random = new Random();
        final int bound = 100500;
        final String password = "password1";

        final User ai = new User(
                "a_ikchurin",
                "tyoma11.95@mail.ru",
                random.nextInt(bound),
                password
        );
        final User ck = new User(
                "cvkucherov",
                "cvkucherov@yandex.ru",
                random.nextInt(bound),
                password
        );
        final User gg = new User(
                "gabolaev",
                "gabolaev98@gmail.com",
                random.nextInt(bound),
                password
        );
        final User ov = new User(
                "venger",
                "farir1408@gmail.com",
                random.nextInt(bound),
                password
        );
        final User ao = new User(
                "a.ostapenko",
                "a.ostapenko@corp.mail.ru",
                bound,
                password
        );
        ao.avatarLink = "https://park.mail.ru/media/avatars/gtp/12/03/87f4d79e36d68c3031ccf6c55e9bbd39.jpg";
        final User dd = new User(
                "d.dorofeev",
                "d.dorofeev@corp.mail.ru",
                bound,
                password
        );
        dd.avatarLink = "https://park.mail.ru/media/avatars/11/17/58d4d1e7b1e97b258c9ed0b37e02d087.jpg";
        final User mt = new User(
                "marina.titova",
                "marina.titova@corp.mail.ru",
                bound,
                password
        );
        mt.avatarLink = "https://park.mail.ru/media/avatars/gtp/02/17/4079016d940210b4ae9ae7d41c4a2065_AQBGC0E.jpg";
        final User at = new User(
                "a.tyuldyukov",
                "a.tyuldyukov@corp.mail.ru",
                bound,
                password
        );
        at.avatarLink = "https://park.mail.ru/media/avatars/gtp/02/23/2e7ceec8361275c4e31fee5fe422740b.jpg";

        allUsers = new HashMap<>();

        allUsers.put(ai.id, ai);
        allUsers.put(ck.id, ck);
        allUsers.put(gg.id, gg);
        allUsers.put(ov.id, ov);
        allUsers.put(ao.id, ao);
        allUsers.put(dd.id, dd);
        allUsers.put(mt.id, mt);
        allUsers.put(at.id, at);

        final int maxFakeUsers = 100;

        for (int i = 0; i < maxFakeUsers; i++) {
            final String email = faker.internet().emailAddress();
            final User fakeUser = new User(
                    email.split("@")[0],
                    email,
                    random.nextInt(bound),
                    password
            );
            allUsers.put(fakeUser.id, fakeUser);
        }
    }

    public boolean hasUser(User user) {
        return allUsers.containsKey(user.id);
    }

    public boolean hasId(Long id) {
        return allUsers.containsKey(id);
    }

    public boolean hasUsername(String username) {
        for (Map.Entry<Long, User> entry : allUsers.entrySet()) {
            if (entry.getValue().username.equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEmail(String email) {
        for (Map.Entry<Long, User> entry : allUsers.entrySet()) {
            if (entry.getValue().email.equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> getByRating(boolean ascending) {
        final List<User> users = new ArrayList<>(allUsers.values());
        users.sort((User u1, User u2) -> ascending ? (u1.rank - u2.rank) : (u2.rank - u1.rank));
        return users;
    }

    @Override
    public User findById(Long id) {
        if (hasId(id)) {
            return allUsers.get(id);
        }
        throw new NotFoundException(String.format("User with id %d not found", id));
    }

    @Override
    public User findByEmail(String email) {
        for (Map.Entry<Long, User> entry : allUsers.entrySet()) {
            final User user = entry.getValue();
            if (user.email.equals(email)) {
                return user;
            }
        }
        throw new NotFoundException(String.format("User with email %s not found", email));
    }

    @Override
    public void save(User user) {
        if (!hasUser(user) && (hasUsername(user.username) || hasEmail(user.email))) {
            throw new InvalidParamsException("Cannot create user: fields username and email should be unique");
        }

        if (user.password.isEmpty()) {
            throw new InvalidParamsException("Password could not be empty");
        }
        if (user.username.isEmpty()) {
            throw new InvalidParamsException("Username could not be empty");
        }
        if (user.email.isEmpty()) {
            throw new InvalidParamsException("Email could not be empty");
        }

        allUsers.put(user.id, user);
    }

    @Override
    public void delete(User user) {
        if (hasUser(user)) {
            allUsers.remove(user.id);
        } else {
            throw new NotFoundException(String.format("User with id %d not found", user.id));
        }
    }

    @Override
    public void partialUpdate(User user, MeController.PartialUpdateRequest requestBody) {
        final User tmpUser = new User();
        tmpUser.id = user.id;

        tmpUser.username = (requestBody.username != null) ? requestBody.username : user.username;
        tmpUser.email = (requestBody.email != null) ? requestBody.email : user.email;
        tmpUser.password = (requestBody.password != null) ? requestBody.password : user.password;
        tmpUser.rank = user.rank;
        save(tmpUser);
    }

    @Override
    public void update(User user, MeController.CreateOrFullUpdateRequest requestBody) {
        user.username = requestBody.username;
        user.email = requestBody.email;
        user.password = requestBody.password;
        save(user);
    }
}
