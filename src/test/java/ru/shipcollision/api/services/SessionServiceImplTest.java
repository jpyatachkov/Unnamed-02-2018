package ru.shipcollision.api.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.shipcollision.api.CorrectUserHelper;
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.models.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {SessionServiceImpl.class, UserServiceImpl.class}
)
@DisplayName("Тест cервиса сессий")
public class SessionServiceImplTest {

    @MockBean
    UserServiceImpl userService;

    @Autowired
    SessionServiceImpl sessionService;

    private MockHttpSession sessionWithUser;

    private MockHttpSession sessionWithoutUser;

    private MockHttpSession getSessionWithIncorrectUser() {
        final MockHttpSession sessionWithIncorrectUserId = new MockHttpSession();
        sessionWithIncorrectUserId.setAttribute(SessionServiceImpl.ATTRIBUTE_NAME, CorrectUserHelper.id + 1);
        return sessionWithIncorrectUserId;
    }

    @BeforeEach
    public void setUpMockSessions() {
        sessionWithoutUser = new MockHttpSession();

        sessionWithUser = new MockHttpSession();
        sessionWithUser.setAttribute(SessionServiceImpl.ATTRIBUTE_NAME, CorrectUserHelper.id);
    }

    @BeforeEach
    public void mockUserService() {
        CorrectUserHelper.mockUserService(userService);
    }

    @Test
    @DisplayName("сессия открывается, в нее записывается id текущего пользователя")
    public void testCanOpenSession() {
        final User correctUser = CorrectUserHelper.getCorrectUser();

        sessionService.openSession(sessionWithoutUser, correctUser);

        Assertions.assertNotNull(sessionWithoutUser.getAttribute(SessionServiceImpl.ATTRIBUTE_NAME));
        Assertions.assertEquals(correctUser.id, sessionWithoutUser.getAttribute(SessionServiceImpl.ATTRIBUTE_NAME));
    }

    @Test
    @DisplayName("можно получить текущего пользователя из открытой сессии")
    public void testCanGetCurrentUserFromOpenedSession() {
        Assertions.assertEquals(CorrectUserHelper.getCorrectUser(), sessionService.getCurrentUser(sessionWithUser));
    }

    @Test
    @DisplayName("нельзя получить пользователя из неоткрытой сессии")
    public void testCanNotGetCurrentUserFromClosedSession() {
        Assertions.assertThrows(ForbiddenException.class, () -> sessionService.getCurrentUser(sessionWithoutUser));
    }

    @Test
    @DisplayName("если в сессии id несуществующего пользователя, будет ошибка, а id из сессии удалится")
    public void testExceptionIfSessionHasIncorrectUserId() {
        final MockHttpSession sessionWithIncorrectUser = getSessionWithIncorrectUser();

        Assertions.assertThrows(
                ForbiddenException.class,
                () -> sessionService.getCurrentUser(sessionWithIncorrectUser)
        );
        Assertions.assertNull(sessionWithIncorrectUser.getAttribute(SessionServiceImpl.ATTRIBUTE_NAME));
    }

    @Test
    @DisplayName("можно закрыть открытую сессию")
    public void testCanCloseOpenedSession() {
        sessionService.closeSession(sessionWithUser);
        Assertions.assertNull(sessionWithUser.getAttribute(SessionServiceImpl.ATTRIBUTE_NAME));
    }

    @Test
    @DisplayName("нельзя закрыть неоткрытую сессию")
    public void testCanNotCloseClosedSession() {
        Assertions.assertThrows(ForbiddenException.class, () -> sessionService.closeSession(sessionWithoutUser));
    }

    @Test
    @DisplayName("если в сессии id несуществующего пользователя, ее не получится закрыть; id из сессии удалится")
    public void testCannotCloseSessionForUnexistingUser() {
        final MockHttpSession sessionWithIncorrectUser = getSessionWithIncorrectUser();

        Assertions.assertThrows(
                ForbiddenException.class,
                () -> sessionService.closeSession(sessionWithIncorrectUser)
        );
        Assertions.assertNull(sessionWithIncorrectUser.getAttribute(SessionServiceImpl.ATTRIBUTE_NAME));
    }
}
