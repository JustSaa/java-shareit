package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.controller.Create;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponseDto> findAllItems(@RequestHeader(Constants.SHARER_USER_ID) Integer userId) {
        log.info("Получен GET запрос к эндпоинту: '/items', Строка параметра запроса для userId: {}", userId);
        return itemService.findAllItems(userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto getById(@RequestHeader(Constants.SHARER_USER_ID) Integer userId,
                                   @PathVariable Integer itemId) {
        log.info("Получен GET запрос к эндпоинту: '/items'," +
                " Строка параметра запроса для userId: {} к itemId: {}", userId, itemId);
        return itemService.findItemByUserIdAndItemId(itemId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(Constants.SHARER_USER_ID) Integer userId,
                          @Validated(Create.class) @RequestBody Item item) {
        log.info("Получен POST запрос к эндпоинту: '/items', Строка параметров запроса: {}", item.toString());
        return ItemMapper.toItemDto(itemService.create(item, userId));
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader(Constants.SHARER_USER_ID) Integer userId,
                          @RequestBody Item item, @PathVariable Integer itemId) {
        log.info("Получен PATCH запрос к эндпоинту: '/items', Строка параметров запроса: {}", item.toString());
        return ItemMapper.toItemDto(itemService.update(item, userId, itemId));
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItem(@RequestHeader(Constants.SHARER_USER_ID) Integer userId,
                                    @RequestParam(name = "text") String text) {
        log.info("Получен GET запрос к эндпоинту: '/search', Строка параметров поиска: {}", text);
        return itemService.searchItem(userId, text);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteItem(@RequestHeader(Constants.SHARER_USER_ID) Integer userId,
                           @PathVariable Integer itemId) {
        log.info("Получен DELETE запрос к эндпоинту: '/items', Строка параметров запроса: userId={}, itemId={}",
                userId, itemId);
        itemService.delete(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto saveComment(@PathVariable("itemId") Integer itemId,
                                          @Valid @RequestBody CommentCreateDto commentCreateDto,
                                          @RequestHeader(Constants.SHARER_USER_ID) Integer userId) {
        log.info("Получен POST запрос к эндпоинту: '/items/{}/comment', " +
                        "Строка параметров запроса: userId={}, Comment={}",
                itemId, userId, commentCreateDto);
        return ItemMapper.toCommentReturnDto(itemService.saveComment(itemId, commentCreateDto, userId));
    }
}
