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
import ru.shipcollision.api.exceptions.UnauthorizedException;
import ru.shipcollision.api.helpers.SessionHelper;
import ru.shipcollision.api.models.AbstractModel;
import ru.shipcollision.api.models.User;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class SessionsController {

    @RequestMapping(path = "/signin", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity doSignin(@RequestBody SigninRequestEntity requestBody,
                                   HttpSession session) throws InvalidCredentialsException {
        final SessionHelper sessionHelper = new SessionHelper(session);
        final List<AbstractModel> foundUsers = User.findByEmail(requestBody.getEmail());
        if (foundUsers.isEmpty()) {
            throw new InvalidCredentialsException();
        }
        final User user = (User) foundUsers.get(0);
        user.comparePasswords(requestBody.getPassword());
        sessionHelper.openSession(user);
        final ApiMessageResponseEntity response = new ApiMessageResponseEntity("You are signed in");
        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(path = "/signout", method = RequestMethod.DELETE)
    public ResponseEntity doSignout(HttpSession session) throws UnauthorizedException, NotFoundException {
        final SessionHelper sessionHelper = new SessionHelper(session);
        sessionHelper.closeSession();
        final ApiMessageResponseEntity response = new ApiMessageResponseEntity("You are signed out");
        return ResponseEntity.ok().body(response);
    }
}
