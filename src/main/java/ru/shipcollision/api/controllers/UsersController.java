package ru.shipcollision.api.controllers;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shipcollision.api.entities.ApiMessageResponseEntity;
import ru.shipcollision.api.entities.ScoreboardResponseEntity;
import ru.shipcollision.api.entities.UserRequestEntity;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.exceptions.PaginationException;
import ru.shipcollision.api.exceptions.PasswordConfirmationException;
import ru.shipcollision.api.helpers.Paginator;
import ru.shipcollision.api.helpers.SessionHelper;
import ru.shipcollision.api.models.AbstractModel;
import ru.shipcollision.api.models.User;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Контроллер API пользователей.
 */
@RestController
@RequestMapping(path = "/users")
public class UsersController {

    @RequestMapping(path = "/scoreboard", method = RequestMethod.GET)
    public ResponseEntity doGetScoreboard(@RequestParam(required = false) Integer offset,
                                          @RequestParam(required = false) Integer limit,
                                          HttpServletRequest request) throws PaginationException {
        offset = (offset == null) ? Paginator.DEFAULT_OFFSET : offset;
        limit = (limit == null) ? Paginator.DEFAULT_LIMIT : limit;

        final Paginator<AbstractModel> paginator = new Paginator<>(User.getByRating(false), offset, limit);
        return ResponseEntity.ok().body(new ScoreboardResponseEntity(
                paginator.paginate(),
                paginator.resolvePrevPageLink(request.getRequestURI()),
                paginator.resolveNextPageLink(request.getRequestURI())
        ));
    }

    /**
     * Создание пользователя
     * !!!
     * Можно создавать повторяющихся пользователей.
     * !!!
     */
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity doPostUser(HttpServletRequest request,
                                     @Valid @RequestBody UserRequestEntity requestBody,
                                     HttpSession session) throws PasswordConfirmationException {
        try {
            final User user = User.fromUserRequestEntity(requestBody);
            user.save();

            new SessionHelper(session).openSession(user);

            final URI location = new URI(String.format("%s/%d/", request.getRequestURI(), user.id));
            return ResponseEntity.created(location).body(user);
        } catch (URISyntaxException error) {
            return ResponseEntity.ok().body(new ApiMessageResponseEntity(
                    "User has been created successfully, no resource URI available"
            ));
        }
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity doGetUser(@PathVariable Integer userId) throws NotFoundException {
        return ResponseEntity.ok().body(User.findById(userId.longValue()));
    }
}
