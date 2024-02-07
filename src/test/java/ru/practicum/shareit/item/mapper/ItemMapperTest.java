package ru.practicum.shareit.item.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class ItemMapperTest {

    private final ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void testToItemDto() {
        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Description");

        ItemDto itemDto = mapper.toItemDto(item);

        assertEquals(1, itemDto.getId());
        assertEquals("Test Item", itemDto.getName());
        assertEquals("Description", itemDto.getDescription());
    }

    @Test
    void testToCommentResponseDto() {
        Comment comment = new Comment();
        comment.setItem(new Item());
        comment.setAuthor(new User());
        comment.setCreated(LocalDateTime.now());

        CommentResponseDto responseDto = mapper.toCommentResponseDto(comment);

        assertEquals(comment.getItem().getName(), responseDto.getItemName());
        assertEquals(comment.getAuthor().getName(), responseDto.getAuthorName());
        assertEquals(comment.getCreated(), responseDto.getCreated());
    }
}
