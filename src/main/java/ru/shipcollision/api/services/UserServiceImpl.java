package ru.shipcollision.api.services;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;
import ru.shipcollision.api.controllers.MeController;
import ru.shipcollision.api.exceptions.InvalidParamsException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;

import java.util.*;

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
        ao.setAvatarLink("https://park.mail.ru/media/avatars/gtp/12/03/87f4d79e36d68c3031ccf6c55e9bbd39.jpg");
        final User dd = new User(
                "d.dorofeev",
                "d.dorofeev@corp.mail.ru",
                bound,
                password
        );
        dd.setAvatarLink("https://park.mail.ru/media/avatars/11/17/58d4d1e7b1e97b258c9ed0b37e02d087.jpg");
        final User mt = new User(
                "marina.titova",
                "marina.titova@corp.mail.ru",
                bound,
                password
        );
        mt.setAvatarLink("https://park.mail.ru/media/avatars/gtp/02/17/4079016d940210b4ae9ae7d41c4a2065_AQBGC0E.jpg");
        final User at = new User(
                "a.tyuldyukov",
                "a.tyuldyukov@corp.mail.ru",
                bound,
                password
        );
        at.setAvatarLink("https://park.mail.ru/media/avatars/gtp/02/23/2e7ceec8361275c4e31fee5fe422740b.jpg");

        allUsers = new HashMap<>();

        allUsers.put(ai.getId(), ai);
        allUsers.put(ck.getId(), ck);
        allUsers.put(gg.getId(), gg);
        allUsers.put(ov.getId(), ov);
        allUsers.put(ao.getId(), ao);
        allUsers.put(dd.getId(), dd);
        allUsers.put(mt.getId(), mt);
        allUsers.put(at.getId(), at);

        final int maxFakeUsers = 100;

        for (int i = 0; i < maxFakeUsers; i++) {
            final String email = faker.internet().emailAddress();
            final User fakeUser = new User(
                    email.split("@")[0],
                    email,
                    random.nextInt(bound),
                    password
            );
            allUsers.put(fakeUser.getId(), fakeUser);
        }
    }

    public boolean hasUser(User user) {
        return allUsers.containsKey(user.getId());
    }

    public boolean hasId(Long id) {
        return allUsers.containsKey(id);
    }

    public boolean hasUsername(String username) {
        for (Map.Entry<Long, User> entry : allUsers.entrySet()) {
            if (entry.getValue().getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEmail(String email) {
        for (Map.Entry<Long, User> entry : allUsers.entrySet()) {
            if (entry.getValue().getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> getByRating(boolean ascending) {
        final List<User> users = new ArrayList<>(allUsers.values());
        users.sort((User u1, User u2) -> ascending ? (u1.getRank() - u2.getRank()) : (u2.getRank() - u1.getRank()));
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
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        throw new NotFoundException(String.format("User with email %s not found", email));
    }

    @Override
    public void save(User user) {
        if (!hasUser(user) && (hasUsername(user.getUsername()) || hasEmail(user.getEmail()))) {
            throw new InvalidParamsException("Cannot create user: fields username and email should be unique");
        }

        if (user.getPassword().isEmpty()) {
            throw new InvalidParamsException("Password could not be empty");
        }
        if (user.getUsername().isEmpty()) {
            throw new InvalidParamsException("Username could not be empty");
        }
        if (user.getEmail().isEmpty()) {
            throw new InvalidParamsException("Email could not be empty");
        }

        allUsers.put(user.getId(), user);
    }

    @Override
    public void delete(User user) {
        if (hasUser(user)) {
            allUsers.remove(user.getId());
        } else {
            throw new NotFoundException(String.format("User with id %d not found", user.getId()));
        }
    }

    @Override
    public void partialUpdate(User user, MeController.PartialUpdateRequest requestBody) {
        final User tmpUser = new User();
        tmpUser.setId(user.getId());

        tmpUser.setUsername((requestBody.username != null) ? requestBody.username : user.getUsername());
        tmpUser.setEmail((requestBody.email != null) ? requestBody.email : user.getEmail());
        tmpUser.setPassword((requestBody.password != null) ? requestBody.password : user.getPassword());
        tmpUser.setRank(user.getRank());
        tmpUser.setAvatarLink(user.getAvatarLink());

        save(tmpUser);
    }

    @Override
    public void update(User user, MeController.CreateOrFullUpdateRequest requestBody) {
        user.setUsername(requestBody.username);
        user.setEmail(requestBody.email);
        user.setPassword(requestBody.password);
        save(user);
    }
}
