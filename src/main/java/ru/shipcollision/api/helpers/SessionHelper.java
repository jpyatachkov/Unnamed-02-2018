package ru.shipcollision.api.helpers;

import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.exceptions.UnauthorizedException;
import ru.shipcollision.api.models.AbstractModel;
import ru.shipcollision.api.models.User;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Хелпер для работы с сессиями.
 */
public class SessionHelper {

    /**
     * Имя куки, в которую будет записан идентификатор сессии.
     */
    public static final String ATTRIBUTE_NAME = "JSESSIONID";

    private HttpSession session;

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
     * @throws UnauthorizedException Возбуждается в случае, если пользователь сессии не установлен.
     */
    public User getCurrentUser() throws UnauthorizedException, NotFoundException {
        final Object userId = session.getAttribute(ATTRIBUTE_NAME);
        if (userId == null) {
            throw new UnauthorizedException();
        }
        return User.findById((Long) userId);
    }

    /**
     * Закрывает сессию для текущего пользователя.
     *
     * @throws UnauthorizedException Возбуждается в случае, если пользователь сессии не установлен.
     */
    public void closeSession() throws UnauthorizedException, NotFoundException {
        if (!sessionHasUser()) {
            throw new UnauthorizedException();
        }
        session.removeAttribute(ATTRIBUTE_NAME);
    }
}
