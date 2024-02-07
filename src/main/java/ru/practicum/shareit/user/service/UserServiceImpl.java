package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.EmailNotUniqueException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public User create(User user) {
        log.debug("Create User: {}", user);
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

    @Transactional
    @Override
    public User update(User user, Integer userId) {
        log.debug("Update User: {}, userId: {}", user, userId);
        userExistenceCheck(userId);
        User userToUpdate = userRepository.findById(userId).get();
        Optional.ofNullable(user.getEmail())
                .filter(email -> !email.equals(userToUpdate.getEmail()) && !email.isBlank())
                .ifPresent(email -> {
                    validationEmail(user);
                    userToUpdate.setEmail(email);
                });
        Optional.ofNullable(user.getName())
                .filter(name -> !name.isBlank())
                .ifPresent(userToUpdate::setName);

        return userToUpdate;
    }

    @Transactional
    @Override
    public void delete(Integer userId) {
        userRepository.deleteById(userId);
    }

    private void userExistenceCheck(Integer id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Нет пользователя с id = " + id);
        }
    }

    public void validationEmail(User user) {
        String userEmail = user.getEmail();

        boolean isEmailValid = userRepository.findAll().stream()
                .noneMatch(existingUser -> existingUser.getEmail().equals(userEmail));

        if (!isEmailValid) {
            throw new DuplicateException("Такой адрес электронной почты уже используется: " + userEmail);
        }
    }
}
