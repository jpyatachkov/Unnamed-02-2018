package ru.shipcollision.api.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.models.User;

/**
 * Тест сервиса сессий.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {SessionServiceImpl.class, UserServiceImpl.class}
)
public class SessionServiceTest {

    @Autowired
    private SessionServiceImpl sessionService;

    @Autowired
    private UserServiceImpl userService;

    @Test
    public void testCanOpenSession() {
        final User user = userService.findById((long) 1);
        final MockHttpSession mockHttpSession = new MockHttpSession();

        sessionService.openSession(mockHttpSession, user);

        Assertions.assertEquals(mockHttpSession.getAttribute(SessionServiceImpl.ATTRIBUTE_NAME), user.id);
    }

    @Test
    public void testCanCloseSession() {
        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionServiceImpl.ATTRIBUTE_NAME, (long) 1);

        sessionService.closeSession(mockHttpSession);

        Assertions.assertNull(mockHttpSession.getAttribute(SessionServiceImpl.ATTRIBUTE_NAME));
    }

    @Test
    public void testGetForbiddenIfNoUserInSession() {
        final MockHttpSession mockHttpSession = new MockHttpSession();
        Assertions.assertThrows(ForbiddenException.class, () -> sessionService.closeSession(mockHttpSession));
    }

    @Test
    public void testGetCurrentUserFromOpenedSession() {
        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SessionServiceImpl.ATTRIBUTE_NAME, (long)1);

        Assertions.assertEquals(sessionService.getCurrentUser(mockHttpSession), userService.findById((long)1));
    }

    @Test
    public void testGetForbiddenWhenNoUserSignedIn() {
        final MockHttpSession mockHttpSession = new MockHttpSession();
        Assertions.assertThrows(ForbiddenException.class, () -> sessionService.getCurrentUser(mockHttpSession));
    }
}
