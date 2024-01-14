package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemCreateDto create(Item item, Integer userId);

    Item update(Item item, Integer userId, Integer itemId);

    Item findByItemId(Integer itemId);

    List<Item> findAllItems(Integer userId);

    void delete(Integer itemId, Integer userId);

    List<Item> searchItem(Integer userId, String text);

    Item findItemByUserIdAndItemId(Integer itemId, Integer userId);
}
