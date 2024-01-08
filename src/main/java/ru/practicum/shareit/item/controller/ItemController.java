package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@Validated
public class ItemController {

    private final ItemService itemService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> findAllItems(@RequestHeader(SHARER_USER_ID) Integer userId) {
        return itemService.findAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public Item getById(@RequestHeader(SHARER_USER_ID) Integer userId,
                        @PathVariable Integer itemId) {
        return itemService.findItemByUserIdAndItemId(itemId, userId);
    }

    @PostMapping
    public Item create(@RequestHeader(SHARER_USER_ID) Integer userId,
                       @Valid @RequestBody Item item) {
        log.info("Получен POST запрос к эндпоинту: '/items', Строка параметров запроса: {}", item.toString());
        return itemService.create(item, userId);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader(SHARER_USER_ID) Integer userId,
                       @RequestBody Item item, @PathVariable Integer itemId) {
        log.info("Получен PATCH запрос к эндпоинту: '/items', Строка параметров запроса: {}", item.toString());
        return itemService.update(item, userId, itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestHeader(SHARER_USER_ID) Integer userId,
                                 @RequestParam(required = false, defaultValue = "") String text) {
        return itemService.searchItem(userId, text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(SHARER_USER_ID) Integer userId,
                           @PathVariable Integer itemId) {
        log.info("Получен DELETE запрос к эндпоинту: '/items', Строка параметров запроса: userId={}, itemId={}",
                userId, itemId);
        itemService.delete(itemId, userId);
    }

}
