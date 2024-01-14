package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserCreateDto create(User user);

    User findById(Integer userId);

    List<User> findAll();

    User update(User user, Integer userId);

    void delete(Integer userId);
}
