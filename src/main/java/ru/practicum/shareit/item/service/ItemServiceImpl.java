package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.UnauthorizedException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.item.mapper.ItemMapper;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemCreateDto create(Item item, Integer userId) {
        userExistenceCheck(userId);
        item.setOwner(userId);
        itemStorage.create(item);
        return ItemMapper.itemCreateDto(item);
    }

    @Override
    public Item update(Item item, Integer userId, Integer itemId) {
        userExistenceCheck(userId);

        Optional<Item> existingItem = itemStorage.findByItemId(itemId);
        if (existingItem.isEmpty()) {
            throw new NotFoundException("Предмет с id:" + itemId + " не найден");
        }

        Item updatedItem = existingItem.get();

        userAuthorizedCheck(updatedItem, userId);

        if (item.getName() != null && !item.getName().isEmpty()) {
            updatedItem.setName(item.getName());
        }

        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            updatedItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }

        itemStorage.update(updatedItem);

        return updatedItem;
    }


    @Override
    public Item findByItemId(Integer itemId) {
        Optional<Item> item = itemStorage.findByItemId(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Нет вещи с id = " + itemId);
        }
        return item.get();
    }

    @Override
    public List<Item> findAllItems(Integer userId) {
        userExistenceCheck(userId);
        return itemStorage.findByUserId(userId);
    }

    @Override
    public Item findItemByUserIdAndItemId(Integer itemId, Integer userId) {
        userExistenceCheck(userId);
        return findByItemId(itemId);
    }

    @Override
    public void delete(Integer itemId, Integer userId) {
        userAuthorizedCheck(findByItemId(itemId), userId);
        itemStorage.delete(itemId, userId);
    }

    public List<Item> searchItem(Integer userId, String text) {
        userExistenceCheck(userId);
        return itemStorage.searchItem(text);
    }

    private void userExistenceCheck(Integer userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Нет пользователя с id =" + userId);
        }
    }

    private void userAuthorizedCheck(Item item, Integer userId) {
        if (!item.getOwner().equals(userId)) {
            throw new UnauthorizedException("Пользователь с id:"
                    + userId + " не имеет права редактировать предмет");
        }
    }
}
