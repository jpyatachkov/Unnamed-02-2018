package ru.shipcollision.api.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.shipcollision.api.dao.UserDAO;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.services.SessionService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Контроллер API пользователей.
 */
@RestController
@RequestMapping(path = "/users")
public class UsersController {

    public static final int DEFAULT_OFFSET = 0;

    public static final int DEFAULT_LIMIT = 10;

    private final SessionService sessionService;

    private final UserDAO userDAO;

    public UsersController(SessionService sessionService,
                           UserDAO userDAO) {
        this.sessionService = sessionService;
        this.userDAO = userDAO;
    }

    @GetMapping(path = "/scoreboard")
    public Scoreboard doGetScoreboard(@RequestParam(required = false) Integer offset,
                                      @RequestParam(required = false) Integer limit) {
        final int currentOffset = (offset == null) ? DEFAULT_OFFSET : offset;
        final int currentLimit = (limit == null) ? DEFAULT_LIMIT : limit;

        final List<User> users = userDAO.getByRating(false, currentOffset, currentLimit);

        final String linkTemplate = "/?offset=%d&limit=%d";
        final Integer usersCount = userDAO.getUsersCount();

        final String prevPageParams = (currentOffset - currentLimit >= 0)
                ? String.format(linkTemplate, currentOffset - currentLimit, currentLimit) : null;
        final String nexPageParams = (currentOffset + currentLimit <= usersCount)
                ? String.format(linkTemplate, currentOffset + currentLimit, currentLimit) : null;

        return new Scoreboard(users, prevPageParams, nexPageParams);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> doPostUser(HttpServletRequest request,
                                        @RequestBody @Valid User user,
                                        HttpSession session) throws URISyntaxException {
        final User savedUser = userDAO.save(user);
        sessionService.openSession(session, savedUser);
        final URI location = new URI(String.format("%s/%d/", request.getRequestURI(), savedUser.id));
        return ResponseEntity.created(location).body(savedUser);
    }

    @GetMapping(path = "/{userId}")
    public User doGetUser(@PathVariable Integer userId) {
        return userDAO.findById(userId.longValue());
    }

    @SuppressWarnings({"PublicField", "unused"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static final class Scoreboard {

        @JsonProperty("users")
        public @NotNull List<User> users;

        @Nullable
        @JsonProperty("prevPage")
        public String prevPageLink;

        @Nullable
        @JsonProperty(value = "nextPage")
        public String nextPageLink;

        public Scoreboard() {
        }

        public Scoreboard(@NotNull List<User> users, String prevPageLink, String nextPageLink) {
            this.users = users;
            this.prevPageLink = prevPageLink;
            this.nextPageLink = nextPageLink;
        }
    }
}
