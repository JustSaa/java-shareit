package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component("memoryItemsStorage")
public class ItemStorageImpl implements ItemStorage {
    private Integer itemId = 0;
    private final Map<Integer, List<Item>> itemStorage = new HashMap<>();

    @Override
    public void create(Item item) {
        itemIdUpdate();
        item.setId(itemId);
        itemStorage.computeIfAbsent(item.getOwner(), k -> new ArrayList<>()).add(item);
    }

    @Override
    public Item update(Item item) {
        Integer owner = item.getOwner();
        List<Item> userItems = itemStorage.computeIfAbsent(owner, k -> new ArrayList<>());
        userItems.removeIf(existingItem -> existingItem.getId().equals(item.getId()));
        userItems.add(item);
        return item;
    }

    @Override
    public Optional<Item> findByItemId(Integer itemId) {
        List<Item> allItems = getAllItems();
        return allItems.stream()
                .filter(item -> Objects.equals(item.getId(), itemId)).findAny();
    }

    @Override
    public List<Item> findAll() {
        return getAllItems();
    }

    public List<Item> findByUserId(Integer userId) {
        return itemStorage.getOrDefault(userId, Collections.emptyList());
    }

    public List<Item> getAllItems() {
        return itemStorage.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer itemId, Integer userId) {
        itemStorage.computeIfPresent(userId, (key, value) -> {
            value.removeIf(item -> item.getId().equals(itemId));
            return value.isEmpty() ? null : value;
        });
    }

    public List<Item> searchItem(String text) {
        List<Item> allItems = getAllItems();

        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return allItems.stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void itemIdUpdate() {
        itemId++;
    }
}
