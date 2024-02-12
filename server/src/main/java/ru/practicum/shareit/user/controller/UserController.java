package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findAll() {
        log.info("Получен GET запрос к эндпоинту: '/users'");
        return userService.findAll().stream()
                .map(UserMapper.INSTANCE::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findById(@PathVariable Integer userId) {
        log.info("Получен GET запрос к эндпоинту: '/users'. Запрос для пользователя с userId: {}", userId);
        return UserMapper.INSTANCE.toUserDto(userService.findById(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody User user) {
        log.info("Получен POST запрос к эндпоинту: '/users', Строка параметров запроса: {}", user.toString());
        return UserMapper.INSTANCE.toUserDto(userService.create(user));
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@RequestBody User user, @PathVariable Integer userId) {
        log.info("Получен PATCH запрос к эндпоинту: '/users', Строка параметров запроса: {}", user.toString());
        return UserMapper.INSTANCE.toUserDto(userService.update(user, userId));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Integer userId) {
        log.info("Получен DELETE запрос к эндпоинту: '/users', userId: {}", userId);
        userService.delete(userId);
    }
}
