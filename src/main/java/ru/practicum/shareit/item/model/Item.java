package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.controller.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Integer id;
    @NotBlank(groups = Create.class, message = "Имя не может быть пустым")
    private String name;
    @NotBlank(groups = Create.class, message = "Описание не может быть пустым")
    private String description;
    @NotNull(groups = Create.class, message = "Введите статус для предмета")
    private Boolean available;
    private Integer owner;
    private ItemRequest request;

    public Item(String name, String description, Boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = new ItemRequest(requestId);
    }

    public Boolean isAvailable() {
        return available;
    }
}
