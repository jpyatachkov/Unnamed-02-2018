package ru.shipcollision.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.shipcollision.api.entities.ApiMessageResponseEntity;
import ru.shipcollision.api.entities.SigninRequestEntity;
import ru.shipcollision.api.exceptions.InvalidCredentialsException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.helpers.SessionHelper;
import ru.shipcollision.api.models.AbstractModel;
import ru.shipcollision.api.models.User;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Контроллер аутентификации и авторизации.
 */
@RestController
public class SessionsController {

    @RequestMapping(path = "/signin", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity doSignin(@RequestBody SigninRequestEntity requestBody,
                                   HttpSession session) throws InvalidCredentialsException {
        final SessionHelper sessionHelper = new SessionHelper(session);

        final List<AbstractModel> foundUsers = User.findByEmail(requestBody.email);
        if (foundUsers.isEmpty()) {
            throw new InvalidCredentialsException();
        }

        final User user = (User) foundUsers.get(0);
        user.comparePasswords(requestBody.password);

        sessionHelper.openSession(user);

        return ResponseEntity.ok().body(new ApiMessageResponseEntity("You are signed in"));
    }

    @RequestMapping(path = "/signout", method = RequestMethod.DELETE)
    public ResponseEntity doSignout(HttpSession session) throws ForbiddenException, NotFoundException {
        new SessionHelper(session).closeSession();
        return ResponseEntity.ok().body(new ApiMessageResponseEntity("You are signed out"));
    }
}
