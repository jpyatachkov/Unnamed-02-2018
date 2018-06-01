package ru.shipcollision.api.services;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import ru.shipcollision.api.dao.UserDAO;
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Сервис для работы с сессиями.
 */
@Service
public class SessionService {

    /**
     * Имя куки, в которую будет записан идентификатор сессии.
     */
    public static final String COOKIE_NAME = "JSESSIONID";

    private final UserDAO userDAO;

    public SessionService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Метод для проверки, что хотя бы один пользователь авторизован.
     *
     * @return Проверяет, установлена ли кука.
     */
    private User getUserFromSession(HttpSession session) {
        try {
            final Object userId = session.getAttribute(COOKIE_NAME);

            if (userId == null) {
                throw new ForbiddenException();
            }

            try {
                return userDAO.findById((Long) userId);
            } catch (NotFoundException e) {
                session.invalidate();
                throw new ForbiddenException(e);
            }
        } catch (IllegalStateException e) {
            //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
            throw new ForbiddenException();
        }
    }

    public User wsGetUserFromSession(WebSocketSession webSocketSession) {
        final Map<String, Object> attributes = webSocketSession.getAttributes();
        if (attributes.containsKey(COOKIE_NAME)) {
            return userDAO.findById((Long) attributes.get(COOKIE_NAME));
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    /**
     * Записывает в куки идентификатор пользователя.
     *
     * @param user Пользователь, для которого будет открыта сессия.
     */
    public void openSession(HttpSession session, User user) {
        session.setAttribute(COOKIE_NAME, user.id);
    }

    /**
     * Возвращает текущего пользователя.
     *
     * @return Пользователь открытой сессии.
     */
    public User getCurrentUser(HttpSession session) {
        return getUserFromSession(session);
    }

    /**
     * Закрывает сессию для текущего пользователя.
     */
    public void closeSession(HttpSession session) {
        session.invalidate();
    }
}
