package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.controller.Create;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Item> findAllItems(@RequestHeader(SHARER_USER_ID) Integer userId) {
        log.info("Получен GET запрос к эндпоинту: '/items', Строка параметра запроса для userId: {}", userId);
        return itemService.findAllItems(userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public Item getById(@RequestHeader(SHARER_USER_ID) Integer userId,
                        @PathVariable Integer itemId) {
        log.info("Получен GET запрос к эндпоинту: '/items'," +
                " Строка параметра запроса для userId: {} к itemId: {}", userId, itemId);
        return itemService.findItemByUserIdAndItemId(itemId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemCreateDto create(@RequestHeader(SHARER_USER_ID) Integer userId,
                                @Validated(Create.class) @RequestBody Item item) {
        log.info("Получен POST запрос к эндпоинту: '/items', Строка параметров запроса: {}", item.toString());
        return itemService.create(item, userId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public Item update(@RequestHeader(SHARER_USER_ID) Integer userId,
                       @RequestBody Item item, @PathVariable Integer itemId) {
        log.info("Получен PATCH запрос к эндпоинту: '/items', Строка параметров запроса: {}", item.toString());
        return itemService.update(item, userId, itemId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> searchItem(@RequestHeader(SHARER_USER_ID) Integer userId,
                                 @RequestParam(defaultValue = "") String text) {
        log.info("Получен GET запрос к эндпоинту: '/search', Строка параметров поиска: {}", text);
        return itemService.searchItem(userId, text);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteItem(@RequestHeader(SHARER_USER_ID) Integer userId,
                           @PathVariable Integer itemId) {
        log.info("Получен DELETE запрос к эндпоинту: '/items', Строка параметров запроса: userId={}, itemId={}",
                userId, itemId);
        itemService.delete(itemId, userId);
    }

}
