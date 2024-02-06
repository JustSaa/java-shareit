package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "requests")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(table = "requests", name = "requester_id")
    private User requester;
    @OneToMany(mappedBy = "itemRequest")
    private List<Item> items;
}