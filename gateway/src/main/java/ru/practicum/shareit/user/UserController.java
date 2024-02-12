package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.group.Create;
import ru.practicum.shareit.CustomValidationException;
import ru.practicum.shareit.group.Update;
import ru.practicum.shareit.user.dto.UserDto;

import static ru.practicum.shareit.constants.Constants.USER_API_PREFIX;

@Slf4j
@RestController
@RequestMapping(USER_API_PREFIX)
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.debug("Creating user: {}", userDto);
        return userClient.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateUser(@PathVariable("userId") Integer userId,
                                             @Validated({Update.class}) @RequestBody UserDto userDto) {
        validate(userDto);
        userDto.setId(userId);
        log.debug("Updating user {}: {}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable("userId") Integer userId) {
        log.debug("Deleting user {}", userId);
        userClient.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUser(@PathVariable("userId") Integer userId) {
        log.debug("Get user {}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAll() {
        log.debug("Get all users");
        return userClient.getAll();
    }

    private void validate(UserDto userDto) {
        if (userDto.getName() != null && userDto.getName().isBlank()) {
            throw new CustomValidationException("Invalid field 'name' for UserDto");
        }
    }
}