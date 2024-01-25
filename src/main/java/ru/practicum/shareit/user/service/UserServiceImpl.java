package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.EmailNotUniqueException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailNotUniqueException("Email should be unique");
        }
    }

    @Override
    public User findById(Integer userId) {
        userExistenceCheck(userId);
        return userRepository.findById(userId).get();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User user, Integer userId) {
        userExistenceCheck(userId);
        User userToUpdate = userRepository.findById(userId).get();
        if (user.getEmail() != null && !user.getEmail().equals(userToUpdate.getEmail()) && !user.getEmail().isBlank()) {
            validationEmail(user);
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            userToUpdate.setName(user.getName());
        }
        //userToUpdate.setId(userId);
        //userRepository.delete(userToUpdate);
        return userRepository.save(userToUpdate);
    }

    @Override
    public void delete(Integer userId) {
        userRepository.deleteById(userId);
    }

    private void userExistenceCheck(Integer id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Нет пользователя с id = " + id);
        }
    }

    private void validationEmail(User user) {
        String userEmail = user.getEmail();

        boolean isEmailValid = userRepository.findAll().stream()
                .noneMatch(existingUser -> existingUser.getEmail().equals(userEmail));

        if (!isEmailValid) {
            throw new DuplicateException("Такой адрес электронной почты уже используется: " + userEmail);
        }
    }
}
