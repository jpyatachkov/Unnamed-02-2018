package ru.shipcollision.api.services;

import ru.shipcollision.api.models.User;

import javax.servlet.http.HttpSession;

public interface SessionService {

    void openSession(HttpSession session, User user);

    void closeSession(HttpSession session);

    User getCurrentUser(HttpSession session);
}
