package ru.shipcollision.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;
import ru.shipcollision.api.entities.UserPartialRequestEntity;
import ru.shipcollision.api.entities.UserRequestEntity;
import ru.shipcollision.api.exceptions.InvalidCredentialsException;
import ru.shipcollision.api.exceptions.NotFoundException;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Модель пользователя (игрока).
 */
@SuppressWarnings({"PublicField", "unused"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends AbstractModel {

    /**
     * Колдекция для хранения пользователей. Временная заглушка.
     */
    private static final Map<Long, AbstractModel> COLLECTION = new HashMap<>() {
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

    @JsonProperty("nickname")
    public @NotNull String nickName;

    @JsonProperty("email")
    public @NotNull String email;

    @JsonProperty("rank")
    public int rank = 0;

    @JsonProperty("avatarLink")
    @Nullable
    public String avatarLink;

    @JsonIgnore
    public @NotNull String passwordHash;

    public User() {

    }

    public User(@NotNull String nickName, @NotNull String email, @NotNull String passwordHash) {
        this.nickName = nickName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public static User fromUserRequestEntity(UserRequestEntity request) {

        final User user = new User();
        user.nickName = request.nickName;
        user.email = request.email;
        user.passwordHash = request.password;
        return user;
    }

    public static List<AbstractModel> getByRating(boolean ascending) {
        final List<AbstractModel> result = new ArrayList<>(COLLECTION.values());

        result.sort((AbstractModel o1, AbstractModel o2) -> {
            final User u1 = (User) o1;
            final User u2 = (User) o2;
            return ascending ? u1.rank - u2.rank : u2.rank - u1.rank;
        });

        return result;
    }

    public static User findById(Long id) throws NotFoundException {
        return (User) findById(COLLECTION, id);
    }

    public static List<AbstractModel> findByEmail(String email) {
        final ArrayList<AbstractModel> result = new ArrayList<>();

        for (Map.Entry<Long, AbstractModel> entry : COLLECTION.entrySet()) {
            final User user = (User) entry.getValue();
            if (user.email.equals(email)) {
                result.add(user);
            }
        }

        return result;
    }

    public void update(UserRequestEntity userFields) {
        nickName = userFields.nickName;
        email = userFields.email;
        passwordHash = userFields.password;
    }

    public void partialUpdate(UserPartialRequestEntity userFields) {
        nickName = (userFields.nickName != null) ? userFields.nickName : nickName;
        email = (userFields.email != null) ? userFields.email : email;
        passwordHash = (userFields.password != null) ? userFields.password: passwordHash;
    }

    public void save() {
        COLLECTION.put(id, this);
    }

    public void delete() {
        COLLECTION.remove(id);
    }

    public void comparePasswords(String otherPassword) throws InvalidCredentialsException {
        final boolean result = passwordHash.equals(otherPassword);
        if (!result) {
            throw new InvalidCredentialsException();
        }
    }
}
