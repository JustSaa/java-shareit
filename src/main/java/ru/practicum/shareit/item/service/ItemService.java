package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(ItemDto item, Integer userId);

    Item update(ItemDto item, Integer userId, Integer itemId);

    List<ItemResponseDto> findAllItems(Integer userId, int from, int size);

    void delete(Integer itemId, Integer userId);

    List<ItemDto> searchItem(Integer userId, String text, int from, int size);

    ItemResponseDto findItemByUserIdAndItemId(Integer itemId, Integer userId);

    Comment saveComment(Integer itemId, CommentCreateDto commentCreateDto, Integer userId);
}
