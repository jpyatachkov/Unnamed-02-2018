package ru.shipcollision.api.services;

import org.springframework.stereotype.Service;
import ru.shipcollision.api.controllers.MeController;
import ru.shipcollision.api.exceptions.InvalidParamsException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private static final Map<Long, User> ALL_USERS = new HashMap<>() {
        {
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

            put(ai.id, ai);
            put(ck.id, ck);
            put(ga.id, ga);
            put(ov.id, ov);
        }
    };

    public boolean hasUser(User user) {
        return ALL_USERS.containsKey(user.id);
    }

    public boolean hasId(Long id) {
        return ALL_USERS.containsKey(id);
    }

    public boolean hasNickName(String nickName) {
        for (Map.Entry<Long, User> entry : ALL_USERS.entrySet()) {
            if (entry.getValue().nickName.equals(nickName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEmail(String email) {
        for (Map.Entry<Long, User> entry : ALL_USERS.entrySet()) {
            if (entry.getValue().email.equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> getByRating(boolean ascending) {
        final List<User> users = new ArrayList<>(ALL_USERS.values());
        users.sort((User u1, User u2) -> ascending ? (u1.rank - u2.rank) : (u2.rank - u1.rank));
        return users;
    }

    @Override
    public User findById(Long id) {
        if (hasId(id)) {
            return ALL_USERS.get(id);
        }
        throw new NotFoundException(String.format("User with id %d not found", id));
    }

    @Override
    public User findByEmail(String email) {
        for (Map.Entry<Long, User> entry : ALL_USERS.entrySet()) {
            final User user = entry.getValue();
            if (user.email.equals(email)) {
                return user;
            }
        }
        throw new NotFoundException(String.format("User with email %s not found", email));
    }

    @Override
    public void save(User user) {
        if (!hasUser(user) && (hasNickName(user.nickName) || hasEmail(user.email))) {
            throw new InvalidParamsException("Cannot create user: fields nickname and email should be unique");
        }

        if (user.nickName.isEmpty()) {
            throw new InvalidParamsException("Nickname could not be empty");
        }
        if (user.email.isEmpty()) {
            throw new InvalidParamsException("Email could not be empty");
        }

        ALL_USERS.put(user.id, user);
    }

    @Override
    public void delete(User user) {
        if (hasUser(user)) {
            ALL_USERS.remove(user.id);
        }
    }

    @Override
    public void partialUpdate(User user, MeController.PartialUpdateRequest requestBody) {
        user.nickName = (requestBody.nickName != null) ? requestBody.nickName : user.nickName;
        user.email = (requestBody.email != null) ? requestBody.email : user.email;
        user.passwordHash = (requestBody.password != null) ? requestBody.password : user.passwordHash;
        save(user);
    }

    @Override
    public void update(User user, MeController.CreateOrFullUpdateRequest requestBody) {
        user.nickName = requestBody.nickName;
        user.email = requestBody.email;
        user.passwordHash = requestBody.password;
        save(user);
    }
}
