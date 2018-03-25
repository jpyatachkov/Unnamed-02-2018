package ru.shipcollision.api.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.services.PaginationService;
import ru.shipcollision.api.services.SessionService;
import ru.shipcollision.api.services.UserService;

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

    private final PaginationService<User> paginationService;

    private final SessionService sessionService;

    private final UserService userService;

    public UsersController(PaginationService<User> paginationService,
                           SessionService sessionService,
                           UserService userService) {
        this.paginationService = paginationService;
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @GetMapping(path = "/scoreboard")
    public ResponseEntity doGetScoreboard(@RequestParam(required = false) Integer offset,
                                          @RequestParam(required = false) Integer limit,
                                          HttpServletRequest request) {
        paginationService.setOffset(offset);
        paginationService.setLimit(limit);
        paginationService.setObjects(userService.getByRating(false));
        return ResponseEntity.ok().body(new Scoreboard(
                paginationService.paginate(),
                paginationService.resolvePrevPageLink(request.getRequestURI()),
                paginationService.resolveNextPageLink(request.getRequestURI())
        ));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity doPostUser(HttpServletRequest request,
                                     @RequestBody @Valid User user,
                                     HttpSession session) throws URISyntaxException {
        userService.save(user);
        sessionService.openSession(session, user);
        final URI location = new URI(String.format("%s/%d/", request.getRequestURI(), user.id));
        return ResponseEntity.created(location).body(user);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity doGetUser(@PathVariable Integer userId) {
        return ResponseEntity.ok().body(userService.findById(userId.longValue()));
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
