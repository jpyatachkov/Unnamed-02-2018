package ru.shipcollision.api.services;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shipcollision.api.controllers.MeController;
import ru.shipcollision.api.models.User;

import java.util.List;

@Service
@Transactional
public class UserServiceDBImpl implements UserService {

    public static final RowMapper<User> USER_ROW_MAPPER = (res, num) -> new User(res.getString("username"),
            res.getString("email"),
            res.getInt("rank"),
            res.getString("avatar_link"),
            res.getString("password"));

    private final JdbcTemplate jdbcTemplate;

    public UserServiceDBImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getByRating(boolean ascending) {
        return null;
    }

    @Override
    public User findById(Long id) {
        final String sqlQuery = "SELECT username, email, rank, avatar_link, password FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, (res, num) ->
                    new User(res.getString("username"), res.getString("email"),
                            res.getInt("rank"), res.getString("avatar_link"),
                            res.getString("password")));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User findByEmail(String email) {
        final String sqlQuery = "SELECT username, email, rank, avatar_link, password FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{email}, (res, num) ->
                    new User(res.getString("username"), res.getString("email"),
                            res.getInt("rank"), res.getString("avatar_link"),
                            res.getString("password")));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void save(User user) {

        if (user.getId() != null) {
            final String sqlQuery = "UPDATE users SET (username, email, rank, avatar_link, password) = " +
                    "values(?, ?, ?, ?, ?) WHERE id=?";
            jdbcTemplate.update(sqlQuery, user.getUsername(),
                    user.getEmail(),
                    user.getRank(),
                    user.getAvatarLink(),
                    user.getPassword(),
                    user.getId());
        } else {
            final String sqlQuery = "INSERT INTO users(username, email, rank, avatar_link, password) " +
                    "values(?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery, user.getUsername(),
                    user.getEmail(),
                    user.getRank(),
                    user.getAvatarLink(),
                    user.getPassword());
        }

    }

    @Override
    public void delete(User user) {

    }

    @Override
    public void partialUpdate(User user, MeController.PartialUpdateRequest requestBody) {
        final String sqlQuery = "UPDATE users SET " +
                "username = coalesce(coalesce(nullif(?, \"\"), username))," +
                "email = coalesce(coalesce(nullif(?, \"\"), email))," +
                "password = coalesce(coalesce(nullif(?, \"\"), password))" +
                "WHERE id = ? " +
                "RETURNING username, email, rank, avatar_link, password";

        String encodedPassword = null;

        if (requestBody.password != null) {
            encodedPassword = BCrypt.hashpw(requestBody.password, BCrypt.gensalt());
        }

        jdbcTemplate.query(sqlQuery,
                new Object[]{
                        requestBody.username,
                        requestBody.email,
                        encodedPassword,
                        user.getId()
                }, USER_ROW_MAPPER);
    }

    @Override
    public void update(User user, MeController.CreateOrFullUpdateRequest requestBody) {
        final String sqlQuery = "UPDATE users SET " +
                "username = ?," +
                "email = ?," +
                "password = ?" +
                "WHERE id = ? " +
                "RETURNING username, email, rank, avatar_link, password";

        String encodedPassword = null;

        if (requestBody.password != null) {
            encodedPassword = BCrypt.hashpw(requestBody.password, BCrypt.gensalt());
        }

        jdbcTemplate.query(sqlQuery,
                new Object[]{
                        requestBody.username,
                        requestBody.email,
                        encodedPassword,
                        user.getId()
                }, USER_ROW_MAPPER);
    }
}
