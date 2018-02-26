package ru.shipcollision.api.controllers;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shipcollision.api.entities.ApiMessageResponseEntity;
import ru.shipcollision.api.entities.ScoreboardResponseEntity;
import ru.shipcollision.api.entities.UserRequestEntity;
import ru.shipcollision.api.exceptions.ApiException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.exceptions.PaginationException;
import ru.shipcollision.api.helpers.Paginator;
import ru.shipcollision.api.helpers.SessionHelper;
import ru.shipcollision.api.models.AbstractModel;
import ru.shipcollision.api.models.User;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
        final List<AbstractModel> usersByRating = User.getByRating(false);
        final Paginator<AbstractModel> paginator = new Paginator<>(usersByRating, offset, limit);
        final ScoreboardResponseEntity response = new ScoreboardResponseEntity(
                paginator.paginate(),
                paginator.resolvePrevPageLink(request.getRequestURI()),
                paginator.resolveNextPageLink(request.getRequestURI())
        );
        return ResponseEntity.ok().body(response);
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
                                     HttpSession session) throws URISyntaxException, ApiException {
        try {
            final User user = User.fromUserRequestEntity(requestBody);
            user.save();
            final SessionHelper sessionHelper = new SessionHelper(session);
            sessionHelper.openSession(user);
            final URI location = new URI(request.getRequestURI() + String.format("/%d/", user.getId()));
            final ApiMessageResponseEntity response = new ApiMessageResponseEntity(
                    "User has been created successfully"
            );
            return ResponseEntity.created(location).body(response);
        } catch (ValidationException error) {
            throw new ApiException(error);
        }
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity doGetUser(@PathVariable Integer userId) throws NotFoundException {
        return ResponseEntity.ok().body(User.findById(userId.longValue()));
    }
}
