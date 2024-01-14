package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.controller.Create;
import ru.practicum.shareit.user.controller.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    private Integer id;
    @NotBlank(groups = Create.class, message = "Имя не должно быть пустым")
    private String name;
    @NotBlank(groups = Create.class, message = "Email не должно быть пустым")
    @Email(groups = {Create.class, Update.class})
    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
