package ru.shipcollision.api.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.shipcollision.api.UserTestFactory;
import ru.shipcollision.api.dao.UserDAO;
import ru.shipcollision.api.exceptions.ForbiddenException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;

import java.util.Arrays;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {SessionService.class, UserDAO.class}
)
@DisplayName("Тест cервиса сессий")
class SessionServiceImplTest {

    private static User correctUser;

    private MockHttpSession session;

    @MockBean
    private UserDAO userDAO;

    @Autowired
    private SessionService sessionService;

    @BeforeAll
    static void setupUserDAO() {
        correctUser = UserTestFactory.createRandomUserWithId((long) 0);
    }

    @BeforeEach
    void setupSession() {
        session = new MockHttpSession();
        session.setAttribute(SessionService.COOKIE_NAME, correctUser.id);
    }

    @Test
    @DisplayName("сессия открывается, в нее записывается id текущего пользователя")
    void testCanOpenSession() {
        sessionService.openSession(session, correctUser);

        Assertions.assertNotNull(session.getAttribute(SessionService.COOKIE_NAME));
        Assertions.assertEquals(correctUser.id, session.getAttribute(SessionService.COOKIE_NAME));
    }

    @Test
    @DisplayName("можно получить текущего пользователя из открытой сессии")
    void testCanGetCurrentUserFromOpenedSession() {
        Mockito.when(userDAO.findById(Mockito.eq(correctUser.id)))
                .thenReturn(correctUser);

        Assertions.assertEquals(correctUser, sessionService.getCurrentUser(session));
    }

    @Test
    @DisplayName("нельзя получить пользователя из неоткрытой сессии")
    void testCanNotGetCurrentUserFromClosedSession() {
        final MockHttpSession sessionWithoutUser = new MockHttpSession();

        Assertions.assertThrows(ForbiddenException.class, () -> sessionService.getCurrentUser(sessionWithoutUser));
    }

    @Test
    @DisplayName("если в сессии id несуществующего пользователя, будет ошибка, а id из сессии удалится")
    void testExceptionIfSessionHasIncorrectUserId() {
        Mockito.when(userDAO.findById(Mockito.eq(correctUser.id)))
                .thenThrow(NotFoundException.class);

        Assertions.assertThrows(
                ForbiddenException.class,
                () -> sessionService.getCurrentUser(session)
        );
        Assertions.assertFalse(Arrays.asList(session.getValueNames()).contains(SessionService.COOKIE_NAME));
    }

    @Test
    @DisplayName("можно закрыть открытую сессию")
    void testCanCloseOpenedSession() {
        Mockito.when(userDAO.findById(Mockito.eq(correctUser.id)))
                .thenReturn(correctUser);

        sessionService.closeSession(session);
        Assertions.assertTrue(session.isInvalid());
    }

    @Test
    @DisplayName("нельзя закрыть неоткрытую сессию")
    void testCanNotCloseClosedSession() {
        Mockito.when(userDAO.findById(Mockito.eq(correctUser.id)))
                .thenThrow(NotFoundException.class);

        Assertions.assertThrows(ForbiddenException.class, () -> sessionService.closeSession(session));
    }

    @Test
    @DisplayName("если в сессии id несуществующего пользователя, ее не получится закрыть; id из сессии удалится")
    void testCannotCloseSessionForUnexistingUser() {
        Mockito.when(userDAO.findById(Mockito.eq(correctUser.id)))
                .thenThrow(NotFoundException.class);

        Assertions.assertThrows(
                ForbiddenException.class,
                () -> sessionService.closeSession(session)
        );
        Assertions.assertFalse(Arrays.asList(session.getValueNames()).contains(SessionService.COOKIE_NAME));
    }
}
