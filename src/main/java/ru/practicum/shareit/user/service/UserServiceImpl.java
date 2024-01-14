package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.DuplicateException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserCreateDto create(User user) {
        validationEmail(user);
        userStorage.create(user);
        return UserMapper.userCreateDto(user);
    }

    @Override
    public User findById(Integer userId) {
        userExistenceCheck(userId);
        return userStorage.findById(userId).get();
    }

    @Override
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User update(User user, Integer userId) {
        userExistenceCheck(userId);
        User userToUpdate = userStorage.findById(userId).get();
        if (user.getEmail() != null && !user.getEmail().equals(userToUpdate.getEmail()) && !user.getEmail().isBlank()) {
            validationEmail(user);
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            userToUpdate.setName(user.getName());
        }
        userStorage.delete(userId);
        return userStorage.update(userToUpdate);
    }

    @Override
    public void delete(Integer userId) {
        userStorage.delete(userId);
    }

    private void userExistenceCheck(Integer id) {
        if (userStorage.findById(id).isEmpty()) {
            throw new NotFoundException("Нет пользователя с id = " + id);
        }
    }

    private void validationEmail(User user) {
        String userEmail = user.getEmail();

        boolean isEmailValid = userStorage.findAll().stream()
                .noneMatch(existingUser -> existingUser.getEmail().equals(userEmail));

        if (!isEmailValid) {
            throw new DuplicateException("Такой адрес электронной почты уже используется: " + userEmail);
        }
    }
}
