package ru.shipcollision.api.services;

import ru.shipcollision.api.controllers.MeController;
import ru.shipcollision.api.models.User;

import java.util.List;

public interface UserService {

    List<User> getByRating(boolean ascending);

    User findById(Long id);

    User findByEmail(String email);

    void save(User user);

    void delete(User user);

    void partialUpdate(User user, MeController.PartialUpdateRequest requestBody);

    void update(User user, MeController.CreateOrFullUpdateRequest requestBody);
}
