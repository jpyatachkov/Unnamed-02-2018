package ru.shipcollision.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.helpers.SessionHelper;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(path = "/me")
public class MeController {

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity doGetMe(HttpSession session) throws ForbiddenException, NotFoundException {
        final SessionHelper sessionHelper = new SessionHelper(session);
        return ResponseEntity.ok().body(sessionHelper.getCurrentUser());
    }
}
