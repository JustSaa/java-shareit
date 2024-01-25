package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item, Integer userId);

    Item update(Item item, Integer userId, Integer itemId);

    Item findByItemId(Integer itemId);

    List<ItemResponseDto> findAllItems(Integer userId);

    void delete(Integer itemId, Integer userId);

    List<ItemDto> searchItem(Integer userId, String text);

    ItemResponseDto findItemByUserIdAndItemId(Integer itemId, Integer userId);

    Comment saveComment(Comment comment);
}
