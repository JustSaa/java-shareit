package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.DuplicateException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User create(User user) {
        validationEmail(user);
        return userStorage.create(user);
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
        if (user.getEmail() != null && !user.getEmail().equals(userToUpdate.getEmail())) {
            validationEmail(user);
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
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
