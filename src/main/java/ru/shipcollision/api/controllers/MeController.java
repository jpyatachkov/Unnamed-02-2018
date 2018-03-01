package ru.shipcollision.api.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.shipcollision.api.models.ApiMessage;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.services.SessionService;
import ru.shipcollision.api.services.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * Контроллер для доступа к методам текущего пользователя.
 */
@RestController
@RequestMapping(path = "/me")
@CrossOrigin(value = "https://ship-collision.herokuapp.com/", allowCredentials = "true")
public class MeController {

    private final SessionService sessionService;

    private final UserService userService;

    public MeController(SessionService sessionService, UserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity doGetMe(HttpSession session) {
        sessionService.setSession(session);
        return ResponseEntity.ok().body(sessionService.getCurrentUser());
    }

    @PatchMapping
    public ResponseEntity doPatchMe(@RequestBody @Valid PartialUpdateRequest updateRequest,
                                    HttpSession session) {
        sessionService.setSession(session);
        final User currentUser = sessionService.getCurrentUser();
        userService.partialUpdate(currentUser, updateRequest);
        return ResponseEntity.ok().body(sessionService.getCurrentUser());
    }

    @PutMapping
    public ResponseEntity doPutMe(@RequestBody @Valid CreateOrFullUpdateRequest updateRequest,
                                  HttpSession session) {
        sessionService.setSession(session);
        final User currentUser = sessionService.getCurrentUser();
        userService.update(currentUser, updateRequest);
        return ResponseEntity.ok().body(currentUser);
    }

    @DeleteMapping
    public ResponseEntity doDeleteMe(HttpSession session) {
        sessionService.setSession(session);
        final User currentUser = sessionService.getCurrentUser();
        sessionService.closeSession();
        userService.delete(currentUser);
        return ResponseEntity.ok().body(new ApiMessage(
                "Your profile has been delete successfully. You are signed out"
        ));
    }

    @SuppressWarnings("PublicField")
    public static final class CreateOrFullUpdateRequest {

        @JsonProperty("nickname")
        public @NotEmpty String nickName;

        @JsonProperty("email")
        public @Email @NotEmpty String email;

        @JsonProperty("password")
        public @Length(min = 6, message = "Password must be at least 6 characters") @NotEmpty String password;
    }

    @SuppressWarnings("PublicField")
    public static final class PartialUpdateRequest {

        @Nullable
        @JsonProperty("nickname")
        public String nickName;

        @Nullable
        @JsonProperty("email")
        public @Email String email;

        @Nullable
        @JsonProperty("password")
        public @Length(min = 6, message = "Password must be at least 6 characters") String password;
    }
}
