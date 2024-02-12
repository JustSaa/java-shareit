package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.group.Create;
import ru.practicum.shareit.group.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
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

    public UserDto(Integer userId) {
        this.id = userId;
    }
}
