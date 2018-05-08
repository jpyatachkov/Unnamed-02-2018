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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

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
    public User doGetMe(HttpSession session) {
        return sessionService.getCurrentUser(session);
    }

    @PatchMapping
    public User doPatchMe(@RequestBody @Valid PartialUpdateRequest updateRequest,
                          HttpSession session) {
        final User currentUser = sessionService.getCurrentUser(session);
        currentUser.username = Optional.ofNullable(updateRequest.username).orElse(currentUser.username);
        currentUser.email = Optional.ofNullable(updateRequest.email).orElse(currentUser.email);
        userDAO.update(currentUser);

        if (updateRequest.password != null) {
            currentUser.password = updateRequest.password;
            userDAO.updatePassword(currentUser);
        }

        return sessionService.getCurrentUser(session);
    }

    @PostMapping(path = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> doUploadAvatar(@RequestPart(value = "avatar") MultipartFile avatar, HttpSession session) {
        final String avatarContentType = avatar.getContentType();

        if (avatarContentType == null || !avatarContentType.matches(AVATAR_CONTENT_TYPE_PATTERN)) {
            throw new UnsupportedMediaTypeStatusException(avatarContentType);
        }

        User currentUser = sessionService.getCurrentUser(session);

        if (fileIOService.fileExists(currentUser.avatarLink)) {
            fileIOService.deleteFile(currentUser.avatarLink);
        }

        currentUser.avatarLink = fileIOService.saveFileAndGetResourcePath(avatar);
        currentUser = userDAO.update(currentUser);

        try {
            assert currentUser.avatarLink != null;
            final URI avatarURI = new URI(currentUser.avatarLink);
            return ResponseEntity.created(avatarURI).body(currentUser);
        } catch (URISyntaxException e) {
            return ResponseEntity.badRequest().body(new ApiMessage("Avatar has been uploaded incorrectly"));
        } catch (AssertionError e) {
            return ResponseEntity.badRequest().body(new ApiMessage("Unable to upload avatar"));
        }
    }

    @DeleteMapping(path = "/avatar")
    public User doDeleteAvatar(HttpSession session) {
        final User currentUser = sessionService.getCurrentUser(session);

        if (fileIOService.fileExists(currentUser.avatarLink)) {
            fileIOService.deleteFile(currentUser.avatarLink);
            userDAO.removeAvatar(currentUser);
            currentUser.avatarLink = null;
        }

        return currentUser;
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
