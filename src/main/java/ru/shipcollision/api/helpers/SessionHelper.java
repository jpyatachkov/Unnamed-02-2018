package ru.shipcollision.api.helpers;

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
    public boolean sessionHasUser() {
        final Object userId = session.getAttribute(ATTRIBUTE_NAME);
        return userId != null && User.findById((Long) userId).size() == 1;
    }

    /**
     * Возвращает текущего пользователя.
     *
     * @return Пользователь открытой сессии.
     * @throws UnauthorizedException Возбуждается в случае, если пользователь сессии не установлен.
     */
    public User getCurrentUser() throws UnauthorizedException {
        final Object userId = session.getAttribute(ATTRIBUTE_NAME);
        if (userId == null) {
            throw new UnauthorizedException();
        }
        final List<AbstractModel> foundUsers = User.findById((Long) userId);
        if (foundUsers.isEmpty()) {
            throw new UnauthorizedException();
        } else if (foundUsers.size() > 1) {
            throw new UnauthorizedException("Invalid cookie. Try to sign in once more");
        } else {
            return (User) foundUsers.get(0);
        }
    }

    /**
     * Закрывает сессию для текущего пользователя.
     *
     * @throws UnauthorizedException Возбуждается в случае, если пользователь сессии не установлен.
     */
    public void closeSession() throws UnauthorizedException {
        if (!sessionHasUser()) {
            throw new UnauthorizedException();
        }
        session.removeAttribute(ATTRIBUTE_NAME);
    }
}
