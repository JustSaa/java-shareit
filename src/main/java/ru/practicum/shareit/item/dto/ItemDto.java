package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.controller.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    private Integer id;
    @NotBlank(groups = Create.class, message = "Имя не может быть пустым")
    private String name;
    @NotBlank(groups = Create.class, message = "Имя не может быть пустым")
    private String description;
    @NotNull(groups = Create.class, message = "Введите статус для предмета")
    private Boolean available;
    private Integer requestId;

    public ItemDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
