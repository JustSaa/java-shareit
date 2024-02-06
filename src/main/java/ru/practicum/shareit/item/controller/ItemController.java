package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.controller.Create;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public List<ItemResponseDto> findAllItems(@RequestHeader(Constants.SHARER_USER_ID) Integer userId,
                                              @PositiveOrZero
                                              @RequestParam(name = "from", defaultValue = Constants.FROM_DEFAULT) int from,
                                              @Positive
                                              @RequestParam(name = "size", defaultValue = Constants.SIZE_DEFAULT) int size) {
        log.info("Получен GET запрос к эндпоинту: '/items', Строка параметра запроса для userId: {}", userId);
        return itemService.findAllItems(userId, from, size);
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
                          @Validated(Create.class) @RequestBody ItemDto item) {
        log.info("Получен POST запрос к эндпоинту: '/items', Строка параметров запроса: {}", item.toString());
        return ItemMapper.toItemDto(itemService.create(item, userId));
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader(Constants.SHARER_USER_ID) Integer userId,
                          @RequestBody ItemDto item, @PathVariable Integer itemId) {
        log.info("Получен PATCH запрос к эндпоинту: '/items', Строка параметров запроса: {}", item.toString());
        return ItemMapper.toItemDto(itemService.update(item, userId, itemId));
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItem(@RequestHeader(Constants.SHARER_USER_ID) Integer userId,
                                    @RequestParam(name = "text", defaultValue = "") String text,
                                    @PositiveOrZero
                                    @RequestParam(name = "from", defaultValue = Constants.FROM_DEFAULT) int from,
                                    @Positive
                                    @RequestParam(name = "size", defaultValue = Constants.SIZE_DEFAULT) int size) {
        log.info("Получен GET запрос к эндпоинту: '/search', Строка параметров поиска: {}", text);
        return itemService.searchItem(userId, text, from, size);
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
        return ItemMapper.toCommentResponseDto(itemService.saveComment(itemId, commentCreateDto, userId));
    }
}
