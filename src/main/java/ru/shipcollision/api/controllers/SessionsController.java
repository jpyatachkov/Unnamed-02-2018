package ru.shipcollision.api.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.exceptions.InvalidCredentialsException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.ApiMessage;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.services.SessionService;
import ru.shipcollision.api.services.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * Контроллер аутентификации и авторизации.
 */
@RestController
public class SessionsController {

    private final SessionService sessionService;

    private final UserService userService;

    public SessionsController(SessionService sessionService, UserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @PostMapping(path = "/signin", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(path = "/signin", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity doSignin(@RequestBody SigninRequest signinRequest,
                                   HttpSession session) throws InvalidCredentialsException {
        sessionService.setSession(session);

        final User user;
        try {
            user = userService.findByEmail(signinRequest.email);
        } catch (NotFoundException error) {
            throw new InvalidCredentialsException(error);
        }

        if (user.passwordHash.equals(signinRequest.password)) {
            sessionService.openSession(user);
            return ResponseEntity.ok().body(new ApiMessage("You are signed in"));
        }
        throw new InvalidCredentialsException();
    }

    @DeleteMapping(path = "/signout")
    public ResponseEntity doSignout(HttpSession session) throws ForbiddenException, NotFoundException {
        sessionService.setSession(session);
        sessionService.closeSession();
        return ResponseEntity.ok().body(new ApiMessage("You are signed out"));
    }

    /**
     * Класс, представляющий запрос на аутентификацию.
     */
    @SuppressWarnings("PublicField")
    public static final class SigninRequest {

        @JsonProperty("email")
        public @Email @NotEmpty String email;

        @JsonProperty("password")
        public @NotEmpty String password;
    }
}
