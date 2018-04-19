package ru.shipcollision.api.dao;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shipcollision.api.controllers.MeController;
import ru.shipcollision.api.exceptions.InvalidCredentialsException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserDAO {

    private static final RowMapper<User> USER_ROW_MAPPER = (res, num) -> new User(
            res.getLong("id"),
            res.getString("username"),
            res.getString("email"),
            res.getInt("rank"),
            res.getString("avatar_link"),
            res.getString("password"));

    private final JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getByRating(boolean ascending) {
        final String order = (ascending) ? "ASC" : "DESC";
        final String sqlQuery = "SELECT id, username, email, rank, avatar_link, password FROM users ORDER BY rank " +
                order;

        List<User> query = jdbcTemplate.query(sqlQuery, new Object[]{}, USER_ROW_MAPPER);
        return query;
    }
    
    public User findById(Long id) {
        final String sqlQuery = "SELECT id, username, email, rank, avatar_link, password FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, (res, num) ->
                    new User(res.getLong("id"), res.getString("username"), res.getString("email"),
                            res.getInt("rank"), res.getString("avatar_link"),
                            res.getString("password")));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("User with id %d not found", id));
        }
    }
    
    public User findByEmail(String email) {
        final String sqlQuery = "SELECT id, username, email, rank, avatar_link, password FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{email}, (res, num) ->
                    new User(res.getLong("id"), res.getString("username"), res.getString("email"),
                            res.getInt("rank"), res.getString("avatar_link"),
                            res.getString("password")));
        } catch (EmptyResultDataAccessException e) {
            throw  new NotFoundException(String.format("User with email %s not found", email));
        }
    }
    
    public User save(User user) {
        if (user.id != null) {
            final String sqlQuery = "UPDATE users SET (username, email, rank, avatar_link, password) = " +
                    "values(?, ?, ?, ?, ?) WHERE id=?";
            jdbcTemplate.update(sqlQuery, user.username,
                    user.email,
                    user.rank,
                    user.avatarLink,
                    user.password,
                    user.id);

            return  user;
        } else {
            final String sqlQuery = "INSERT INTO users(username, email, rank, avatar_link, password) " +
                    "values(?, ?, ?, ?, ?) RETURNING id, username, email, rank, avatar_link, password";

            try {
                List<User> query = jdbcTemplate.query(sqlQuery, new Object[]{user.username,
                        user.email, user.rank, user.avatarLink, user.password}, USER_ROW_MAPPER);
                return query.get(0);
            } catch (Throwable e) {
                throw new InvalidCredentialsException();
            }
        }
    }
    
    public User partialUpdate(Long id, MeController.PartialUpdateRequest updateRequest) {
        final String sqlQuery = "UPDATE users SET " +
                "username = coalesce(coalesce(nullif(?, \"\"), username))," +
                "email = coalesce(coalesce(nullif(?, \"\"), email))," +
                "password = coalesce(coalesce(nullif(?, \"\"), password))" +
                "WHERE id = ? " +
                "RETURNING id, username, email, rank, avatar_link, password";

        String encodedPassword = null;

        if (updateRequest.password != null) {
            encodedPassword = BCrypt.hashpw(updateRequest.password, BCrypt.gensalt());
        }

        List<User> query = jdbcTemplate.query(sqlQuery,
                new Object[]{
                        updateRequest.username,
                        updateRequest.email,
                        encodedPassword,
                        id
                }, USER_ROW_MAPPER);
        return query.get(0);
    }
}
