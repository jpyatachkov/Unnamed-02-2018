package ru.shipcollision.api.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.shipcollision.api.dao.UserDAO;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.services.SessionService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * Контроллер аутентификации и авторизации.
 */
@RestController
public class SessionsController {

    private final SessionService sessionService;

    private final UserDAO userDAO;

    public SessionsController(SessionService sessionService, UserDAO userDAO) {
        this.sessionService = sessionService;
        this.userDAO = userDAO;
    }

    @PostMapping(path = "/signin", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User doSignin(@RequestBody @Valid SigninRequest signinRequest,
                         HttpSession session) {
        final User user = userDAO.authenticate(signinRequest.email, signinRequest.password);
        sessionService.openSession(session, user);
        return user;
    }

    @DeleteMapping(path = "/signout")
    public User doSignout(HttpSession session) {
        final User currentUser = sessionService.getCurrentUser(session);
        sessionService.closeSession(session);
        return currentUser;
    }

    /**
     * Класс, представляющий запрос на аутентификацию.
     */
    @SuppressWarnings({"PublicField", "unused"})
    public static final class SigninRequest {

        @JsonProperty("email")
        public @Email @NotEmpty String email;

        @JsonProperty("password")
        public @NotEmpty String password;

        public SigninRequest() {
        }

        public SigninRequest(@Email @NotEmpty String email,
                             @NotEmpty String password) {
            this.email = email;
            this.password = password;
        }
    }
}
