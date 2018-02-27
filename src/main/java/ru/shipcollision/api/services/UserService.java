package ru.shipcollision.api.services;

import ru.shipcollision.api.controllers.MeController;
import ru.shipcollision.api.exceptions.InvalidParamsException;
import ru.shipcollision.api.exceptions.NotFoundException;
import ru.shipcollision.api.models.User;

import java.util.List;

public interface UserService {

    List<User> getByRating(boolean ascending);

    User findById(Long id) throws NotFoundException;

    User findByEmail(String email) throws NotFoundException;

    void save(User user) throws InvalidParamsException;

    void delete(User user);

    void partialUpdate(User user, MeController.PartialUpdateRequest requestBody) throws InvalidParamsException;

    void update(User user, MeController.CreateOrFullUpdateRequest requestBody) throws InvalidParamsException;
}
