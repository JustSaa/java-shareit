package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    void create(Item item);

    Item update(Item item);

    Optional<Item> findByItemId(Integer itemId);

    List<Item> findByUserId(Integer userId);

    List<Item> findAll();

    List<Item> searchItem(String name);

    void delete(Integer itemId, Integer userId);

}
