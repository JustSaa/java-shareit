package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.CustomValidationException;
import ru.practicum.shareit.group.Create;
import ru.practicum.shareit.group.Update;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;

import static ru.practicum.shareit.constants.Constants.*;

@Slf4j
@Validated
@RestController
@RequestMapping(ITEM_API_PREFIX)
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveItem(@RequestHeader(SHARER_USER_ID) Integer userId,
                                           @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.debug("Creating item: userId={}, body: {}", userId, itemDto);
        return itemClient.saveItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateItem(@RequestHeader(SHARER_USER_ID) Integer userId,
                                             @PathVariable("itemId") Integer itemId,
                                             @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        validate(itemDto);
        log.debug("Updating item {}: userId={}, body: {}", itemId, userId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItem(@RequestHeader(SHARER_USER_ID) Integer userId,
                                          @PathVariable("itemId") Integer itemId) {
        log.debug("Get item {}: userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getMyItems(@RequestHeader(SHARER_USER_ID) Integer userId,
                                             @PositiveOrZero
                                             @RequestParam(name = "from", defaultValue = FROM_DEFAULT) Integer from,
                                             @Positive
                                             @RequestParam(name = "size", defaultValue = SIZE_DEFAULT) Integer size) {
        log.debug("Get items that user {} owns: from={}, size={}", userId, from, size);
        return itemClient.getMyItems(userId, from, size);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> search(@RequestHeader(SHARER_USER_ID) Integer userId,
                                         @RequestParam(name = "text", defaultValue = "") String text,
                                         @PositiveOrZero
                                         @RequestParam(name = "from", defaultValue = FROM_DEFAULT) Integer from,
                                         @Positive
                                         @RequestParam(name = "size", defaultValue = SIZE_DEFAULT) Integer size) {
        log.debug("Get items by template: userId={}, from={}, size={}, text={}", userId, from, size, text);
        if (text == null || text.isBlank()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> saveComment(@PathVariable("itemId") Integer itemId,
                                              @Valid @RequestBody CommentCreateDto commentCreateDto,
                                              @RequestHeader(SHARER_USER_ID) Integer userId) {
        log.debug("Creating comment: itemId={}, userId={}, body: {}", itemId, userId, commentCreateDto);
        return itemClient.saveComment(itemId, commentCreateDto, userId);
    }

    private void validate(ItemDto itemDto) {
        if (itemDto.getName() != null && itemDto.getName().isBlank()) {
            throw new CustomValidationException("Invalid field 'name' for ItemDto");
        }
        if (itemDto.getDescription() != null && itemDto.getDescription().isBlank()) {
            throw new CustomValidationException("Invalid field 'description' for ItemDto");
        }
    }
}