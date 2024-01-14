package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAll() {
        log.info("Получен GET запрос к эндпоинту: '/users'");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User findById(@PathVariable Integer userId) {
        log.info("Получен GET запрос к эндпоинту: '/users'. Запрос для пользователя с userId: {}", userId);
        return userService.findById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCreateDto create(@Validated(Create.class) @RequestBody User user) {
        log.info("Получен POST запрос к эндпоинту: '/users', Строка параметров запроса: {}", user.toString());
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@Validated(Update.class) @RequestBody User user, @PathVariable Integer userId) {
        log.info("Получен PATCH запрос к эндпоинту: '/users', Строка параметров запроса: {}", user.toString());
        return userService.update(user, userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Integer userId) {
        log.info("Получен DELETE запрос к эндпоинту: '/users', userId: {}", userId);
        userService.delete(userId);
    }
}
