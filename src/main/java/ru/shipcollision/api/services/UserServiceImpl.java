package ru.shipcollision.api.services;

import org.springframework.stereotype.Service;
import ru.shipcollision.api.controllers.MeController;
import ru.shipcollision.api.exceptions.InvalidParamsException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, User> allUsers;

    public UserServiceImpl() {
        final Random random = new Random();
        final int bound = 100500;

        final User ai = new User("a_ikchurin", "tyoma11.95@mail.ru", "pswd");
        ai.rank = random.nextInt(bound);
        final User ck = new User("cvkucherov", "cvkucherov@yandex.ru", "pswd");
        ck.rank = random.nextInt(bound);
        final User ga = new User("gabolaev", "gabolaev98@gmail.com", "pswd");
        ga.rank = random.nextInt(bound);
        final User ov = new User("venger", "farir1408@gmail.com", "pswd");
        ov.rank = random.nextInt(bound);

        allUsers = new HashMap<>();

        allUsers.put(ai.id, ai);
        allUsers.put(ck.id, ck);
        allUsers.put(ga.id, ga);
        allUsers.put(ov.id, ov);
    }

    public boolean hasUser(User user) {
        return allUsers.containsKey(user.id);
    }

    public boolean hasId(Long id) {
        return allUsers.containsKey(id);
    }

    public boolean hasusername(String username) {
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
        if (!hasUser(user) && (hasusername(user.username) || hasEmail(user.email))) {
            throw new InvalidParamsException("Cannot create user: fields username and email should be unique");
        }

        if (user.username.isEmpty()) {
            throw new InvalidParamsException("username could not be empty");
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
        }
    }

    @Override
    public void partialUpdate(User user, MeController.PartialUpdateRequest requestBody) {
        final User tmpUser = new User();
        tmpUser.id = user.id;

        tmpUser.username = (requestBody.username != null) ? requestBody.username : user.username;
        tmpUser.email = (requestBody.email != null) ? requestBody.email : user.email;
        tmpUser.password = (requestBody.password != null) ? requestBody.password : user.password;

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
