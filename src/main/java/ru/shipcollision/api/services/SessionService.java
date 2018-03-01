package ru.shipcollision.api.services;

import ru.shipcollision.api.models.User;

import javax.servlet.http.HttpSession;

public interface SessionService {

    void openSession(User user);

    void closeSession();

    User getCurrentUser();

    void setSession(HttpSession session);
}
