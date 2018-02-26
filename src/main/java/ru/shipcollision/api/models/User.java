package ru.shipcollision.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;
import ru.shipcollision.api.entities.UserRequestEntity;
import ru.shipcollision.api.exceptions.InvalidCredentialsException;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Модель пользователя (игрока).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends AbstractModel {

    /**
     * Колдекция для хранения пользователей. Временная заглушка.
     */
    private static Map<Long, AbstractModel> collection = new HashMap<>() {
        {
            final Random random = new Random();
            final int bound = 100500;

            final User ai = new User("a_ikchurin", "tyoma11.95@mail.ru", "pswd");
            ai.setRank(random.nextInt(bound));
            final User ck = new User("cvkucherov", "cvkucherov@yandex.ru", "pswd");
            ck.setRank(random.nextInt(bound));
            final User ge = new User("gabolaev", "gabolaev98@gmail.com", "pswd");
            ge.setRank(random.nextInt(bound));
            final User ov = new User("venger", "farir1408@gmail.com", "pswd");
            ov.setRank(random.nextInt(bound));

            put(ai.getId(), ai);
            put(ck.getId(), ck);
            put(ge.getId(), ge);
            put(ov.getId(), ov);
        }
    };

    @JsonProperty("nickname")
    private @NotNull String nickName;

    @JsonProperty("email")
    private @NotNull String email;

    @JsonProperty("rank")
    private int rank = 0;

    @JsonProperty("avatarLink")
    @Nullable
    private String avatarLink;

    @JsonIgnore
    private @NotNull String passwordHash;

    @SuppressWarnings("unused")
    public User() {
        super();
    }

    @SuppressWarnings("unused")
    public User(String nickName, String email, String passwordHash) {
        super();
        this.nickName = nickName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public static User fromUserRequestEntity(UserRequestEntity request) throws ValidationException {
        if (!request.passwordConfirmed()) {
            throw new ValidationException("Password confirmation failed");
        }

        final User user = new User();
        user.nickName = request.getNickName();
        user.email = request.getEmail();
        user.passwordHash = request.getPassword();

        return user;
    }

    public static List<AbstractModel> getByRating(boolean ascending) {
        final List<AbstractModel> result = new ArrayList<>(collection.values());
        result.sort((AbstractModel o1, AbstractModel o2) -> {
            final User u1 = (User) o1;
            final User u2 = (User) o2;
            return ascending ? u1.rank - u2.rank : u2.rank - u1.rank;
        });
        return result;
    }

    @SuppressWarnings("unused")
    public static List<AbstractModel> findById(Long id) {
        return findById(collection, id);
    }

    public static List<AbstractModel> findByEmail(String email) {
        final ArrayList<AbstractModel> result = new ArrayList<>();
        for (Map.Entry<Long, AbstractModel> entry : collection.entrySet()) {
            final User user = (User) entry.getValue();
            if (user.email.equals(email)) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public void save() {
        collection.put(getId(), this);
    }

    @SuppressWarnings("unused")
    public String getNickName() {
        return nickName;
    }

    @SuppressWarnings("unused")
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }

    @SuppressWarnings("unused")
    public void setEmail(String email) {
        this.email = email;
    }

    @SuppressWarnings("unused")
    public int getRank() {
        return rank;
    }

    @SuppressWarnings("unused")
    public void setRank(int rank) {
        this.rank = rank;
    }

    @SuppressWarnings("unused")
    public String getAvatarLink() {
        return avatarLink;
    }

    @SuppressWarnings("unused")
    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    @SuppressWarnings("unused")
    public String getPasswordHash() {
        return passwordHash;
    }

    @SuppressWarnings("unused")
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void comparePasswords(String otherPassword) throws InvalidCredentialsException {
        final boolean result = passwordHash.equals(otherPassword);
        if (!result) {
            throw new InvalidCredentialsException();
        }
    }
}
