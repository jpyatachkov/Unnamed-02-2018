package ru.shipcollision.api.services;

import org.springframework.stereotype.Service;
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.models.User;

import javax.servlet.http.HttpSession;

/**
 * Сервис для работы с сессиями.
 */
@Service
public class SessionService {

    /**
     * Имя куки, в которую будет записан идентификатор сессии.
     */
    public static final String ATTRIBUTE_NAME = "JSESSIONID";

    private final UserService userService;

    private HttpSession session;

    public SessionService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Записывает в куки идентификатор пользователя.
     *
     * @param user Пользователь, для которого будет открыта сессия.
     */
    public void openSession(User user) {
        session.setAttribute(ATTRIBUTE_NAME, user.id);
    }

    /**
     * Метод для проверки, что хотя бы один пользователь авторизован.
     *
     * @return Проверяет, установлена ли кука.
     */
    public boolean sessionHasUser() {
        final Object userId = session.getAttribute(ATTRIBUTE_NAME);
        return userId != null && userService.findById((Long) userId) != null;
    }

    /**
     * Возвращает текущего пользователя.
     *
     * @return Пользователь открытой сессии.
     */
    public User getCurrentUser() {
        final Object userId = session.getAttribute(ATTRIBUTE_NAME);
        if (userId == null) {
            throw new ForbiddenException();
        }
        return userService.findById((Long) userId);
    }

    /**
     * Закрывает сессию для текущего пользователя.
     */
    public void closeSession() {
        if (!sessionHasUser()) {
            throw new ForbiddenException();
        }
        session.removeAttribute(ATTRIBUTE_NAME);
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }
}
