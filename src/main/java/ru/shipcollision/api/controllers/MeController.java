package ru.shipcollision.api.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import ru.shipcollision.api.dao.UserDAO;
import ru.shipcollision.api.models.ApiMessage;
import ru.shipcollision.api.models.User;
import ru.shipcollision.api.services.FileIOService;
import ru.shipcollision.api.services.SessionService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Контроллер для доступа к методам текущего пользователя.
 */
@RestController
@RequestMapping(path = "/me")
public class MeController {

    private static final String AVATAR_CONTENT_TYPE_PATTERN = "^image/.+";

    private final FileIOService fileIOService;

    private final SessionService sessionService;

    private final UserDAO userDAO;

    public MeController(FileIOService fileIOService,
                        SessionService sessionService,
                        UserDAO userDAO) {
        this.fileIOService = fileIOService;
        this.sessionService = sessionService;
        this.userDAO = userDAO;
    }

    @GetMapping
    public ResponseEntity doGetMe(HttpSession session) {
        return ResponseEntity.ok().body(sessionService.getCurrentUser(session));
    }

    @PatchMapping
    public ResponseEntity doPatchMe(@RequestBody @Valid PartialUpdateRequest updateRequest,
                                    HttpSession session) {
        userDAO.partialUpdate(sessionService.getCurrentUser(session).id, updateRequest);
        return ResponseEntity.ok().body(sessionService.getCurrentUser(session));
    }

    @PostMapping(path = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> doUploadAvatar(@RequestPart(value = "avatar") MultipartFile avatar, HttpSession session) {
        final String avatarContentType = avatar.getContentType();

        if (!avatarContentType.matches(AVATAR_CONTENT_TYPE_PATTERN)) {
            throw new UnsupportedMediaTypeStatusException(avatarContentType);
        }

        final User currentUser = sessionService.getCurrentUser(session);

        if (fileIOService.fileExists(currentUser.avatarLink)) {
            fileIOService.deleteFile(currentUser.avatarLink);
        }

        currentUser.avatarLink = fileIOService.saveFileAndGetResourcePath(avatar);

        try {
            final URI avatarURI = new URI(currentUser.avatarLink);
            return ResponseEntity.created(avatarURI).body(currentUser);
        } catch (URISyntaxException e) {
            return ResponseEntity.ok().body(new ApiMessage(
                    "User has been created successfully, no resource URI available"
            ));
        }
    }

    @DeleteMapping(path = "/avatar")
    public ResponseEntity<?> doDeleteAvatar(HttpSession session) {
        final User currentUser = sessionService.getCurrentUser(session);

        if (fileIOService.fileExists(currentUser.avatarLink)) {
            fileIOService.deleteFile(currentUser.avatarLink);
            currentUser.avatarLink = null;
        }

        return ResponseEntity.ok().body(currentUser);
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
