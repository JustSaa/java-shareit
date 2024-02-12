package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.Constants.*;

@Slf4j
@Validated
@RestController
@RequestMapping(REQUEST_API_PREFIX)
@RequiredArgsConstructor
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveRequest(@RequestHeader(SHARER_USER_ID) Integer userId,
                                              @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.debug("Creating itemRequest: userId={}, body: {}", userId, itemRequestCreateDto);
        return requestClient.saveRequest(userId, itemRequestCreateDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getMyRequests(@RequestHeader(SHARER_USER_ID) Integer userId) {
        log.debug("Get requests that user {} made", userId);
        return requestClient.getMyRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAlienRequests(@RequestHeader(SHARER_USER_ID) Integer userId,
                                                   @PositiveOrZero
                                                   @RequestParam(name = "from", defaultValue = FROM_DEFAULT)
                                                   Integer from,
                                                   @Positive
                                                   @RequestParam(name = "size", defaultValue = SIZE_DEFAULT)
                                                   Integer size) {
        log.debug("Get requests that others made: userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAlienRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequestById(@RequestHeader(SHARER_USER_ID) Integer userId,
                                                 @PathVariable("requestId") Integer requestId) {
        log.debug("Get request {}: userId={}", requestId, userId);
        return requestClient.getRequestById(userId, requestId);
    }
}