package ru.shipcollision.api.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.shipcollision.api.models.ApiMessage;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.services.FileIOService;
import ru.shipcollision.api.services.SessionService;
import ru.shipcollision.api.services.UserService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

/**
 * Контроллер для доступа к методам текущего пользователя.
 */
@RestController
@RequestMapping(path = "/me")
public class MeController {

    private final FileIOService fileIOService;

    private final ServletContext servletContext;

    private final SessionService sessionService;

    private final UserService userService;

    public MeController(FileIOService fileIOService,
                        ServletContext servletContext,
                        SessionService sessionService,
                        UserService userService) {
        this.fileIOService = fileIOService;
        this.servletContext = servletContext;
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity doGetMe(HttpSession session) {
        return ResponseEntity.ok().body(sessionService.getCurrentUser(session));
    }

    @PatchMapping
    public ResponseEntity doPatchMe(@RequestBody @Valid PartialUpdateRequest updateRequest,
                                    HttpSession session) {
        final User currentUser = sessionService.getCurrentUser(session);
        userService.partialUpdate(currentUser, updateRequest);
        return ResponseEntity.ok().body(sessionService.getCurrentUser(session));
    }

    @PutMapping
    public ResponseEntity doPutMe(@RequestBody @Valid CreateOrFullUpdateRequest updateRequest,
                                  HttpSession session) {
        final User currentUser = sessionService.getCurrentUser(session);
        userService.update(currentUser, updateRequest);
        return ResponseEntity.ok().body(currentUser);
    }

    @DeleteMapping
    public ResponseEntity doDeleteMe(HttpSession session) {
        final User currentUser = sessionService.getCurrentUser(session);
        sessionService.closeSession(session);
        userService.delete(currentUser);
        return ResponseEntity.ok().body(new ApiMessage(
                "Your profile has been delete successfully. You are signed out"
        ));
    }

    @PostMapping(path = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> doUploadAvatar(@RequestParam("file") MultipartFile avatar, HttpSession session) {
        final User currentUser = sessionService.getCurrentUser(session);
        currentUser.avatarLink = fileIOService.saveAndGetPath(avatar);

        try {
            final URI avatarUri = new URI(currentUser.avatarLink);
            return ResponseEntity.created(avatarUri).body(currentUser);
        } catch (URISyntaxException e) {
            return ResponseEntity.ok().body(new ApiMessage(
                    "User has been created successfully, no resource URI available"
            ));
        }
    }

    @SuppressWarnings("PublicField")
    public static final class CreateOrFullUpdateRequest {

        @JsonProperty("nickname")
        public @NotEmpty String username;

        @JsonProperty("email")
        public @Email @NotEmpty String email;

        @JsonProperty("password")
        public @Length(min = 6, message = "Password must be at least 6 characters") @NotEmpty String password;
    }

    @SuppressWarnings("PublicField")
    public static final class PartialUpdateRequest {

        @Nullable
        @JsonProperty("username")
        public String username;

        @Nullable
        @JsonProperty("email")
        public @Email String email;

        @Nullable
        @JsonProperty("password")
        public @Length(min = 6, message = "Password must be at least 6 characters") String password;
    }
}
