package ru.shipcollision.api.helpers;

import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.models.User;

import javax.servlet.http.HttpSession;

/**
 * Хелпер для работы с сессиями.
 */
public class SessionHelper {

    /**
     * Имя куки, в которую будет записан идентификатор сессии.
     */
    public static final String ATTRIBUTE_NAME = "JSESSIONID";

    private final HttpSession session;

    public SessionHelper(HttpSession session) {
        this.session = session;
    }

    /**
     * Записывает в куки идентификатор пользователя.
     *
     * @param user Пользователь, для которого будет открыта сессия.
     */
    public void openSession(User user) {
        session.setAttribute(ATTRIBUTE_NAME, user.getId());
    }

    /**
     * Метод для проверки, что хотя бы один пользователь авторизован.
     *
     * @return Проверяет, установлена ли кука.
     */
    public boolean sessionHasUser() throws NotFoundException {
        final Object userId = session.getAttribute(ATTRIBUTE_NAME);
        return userId != null && User.findById((Long) userId) != null;
    }

    /**
     * Возвращает текущего пользователя.
     *
     * @return Пользователь открытой сессии.
     * @throws ForbiddenException Возбуждается в случае, если пользователь сессии не установлен.
     */
    public User getCurrentUser() throws ForbiddenException, NotFoundException {
        final Object userId = session.getAttribute(ATTRIBUTE_NAME);
        if (userId == null) {
            throw new ForbiddenException();
        }
        return User.findById((Long) userId);
    }

    /**
     * Закрывает сессию для текущего пользователя.
     *
     * @throws ForbiddenException Возбуждается в случае, если пользователь сессии не установлен.
     */
    public void closeSession() throws ForbiddenException, NotFoundException {
        if (!sessionHasUser()) {
            throw new ForbiddenException();
        }
        session.removeAttribute(ATTRIBUTE_NAME);
    }
}
