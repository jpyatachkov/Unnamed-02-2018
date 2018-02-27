package ru.shipcollision.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.shipcollision.api.entities.ApiMessageResponseEntity;
import ru.shipcollision.api.entities.UserPartialRequestEntity;
import ru.shipcollision.api.entities.UserRequestEntity;
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.helpers.SessionHelper;
import ru.shipcollision.api.models.User;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * Контроллер для доступа к методам текущего пользователя.
 */
@RestController
@RequestMapping(path = "/me")
public class MeController {

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity doGetMe(HttpSession session) throws ForbiddenException, NotFoundException {
        return ResponseEntity.ok().body(new SessionHelper(session).getCurrentUser());
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity doPutMe(@RequestBody @Valid UserRequestEntity updateUserRequest,
                                  HttpSession session) throws ForbiddenException,
            NotFoundException {
        final User currentUser = new SessionHelper(session).getCurrentUser();
        currentUser.update(updateUserRequest);
        currentUser.save();
        return ResponseEntity.ok().body(currentUser);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity doPathMe(@RequestBody @Valid UserPartialRequestEntity updateUserRequest,
                                   HttpSession session) throws ForbiddenException,
            NotFoundException {
        final User currentUser = new SessionHelper(session).getCurrentUser();
        currentUser.partialUpdate(updateUserRequest);
        currentUser.save();
        return ResponseEntity.ok().body(currentUser);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity doDeleteMe(HttpSession session) throws ForbiddenException, NotFoundException {
        final SessionHelper sessionHelper = new SessionHelper(session);
        final User currentUser = sessionHelper.getCurrentUser();
        sessionHelper.closeSession();
        currentUser.delete();
        return ResponseEntity.ok().body(new ApiMessageResponseEntity(
                "Your profile has been delete successfully. You are signed out"
        ));
    }
}
