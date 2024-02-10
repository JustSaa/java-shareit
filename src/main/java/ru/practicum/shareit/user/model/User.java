package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.controller.Create;
import ru.practicum.shareit.user.controller.Update;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
@Builder(toBuilder = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(groups = Create.class, message = "Имя не должно быть пустым")
    private String name;
    @NotNull(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    @Column(unique = true)
    private String email;
    @OneToMany(mappedBy = "owner")
    private List<Item> items = new ArrayList<>();
    @OneToMany(mappedBy = "author")
    private List<Comment> comments = new ArrayList<>();
}
