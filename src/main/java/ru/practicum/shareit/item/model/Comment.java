package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(table = "comments", name = "item_id")
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(table = "comments", name = "author_id")
    private User author;
    private LocalDateTime created;
}