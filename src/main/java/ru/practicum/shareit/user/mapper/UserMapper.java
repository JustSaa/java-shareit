package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getName(), user.getEmail());
    }

    public static UserCreateDto userCreateDto(User user) {
        return new UserCreateDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
