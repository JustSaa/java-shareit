package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestService requestService;
    private final UserService userService;

    @PostMapping
    public ItemRequestResponseDto saveRequest(@RequestHeader(Constants.SHARER_USER_ID) Integer userId,
                                              @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.debug("Получен POST запрос к эндпоинту: '/requests', Строка параметра запроса для userId={} body: {}", userId, itemRequestCreateDto);
        return RequestMapper.toRequestDto(requestService.saveRequest(userId, itemRequestCreateDto));
    }

    @GetMapping
    public List<ItemRequestResponseDto> getMyRequests(@RequestHeader(Constants.SHARER_USER_ID) Integer userId) {
        log.debug("Получен GET запрос к эндпоинту: '/requests', Строка параметра запроса для userId={}", userId);
        User requester = userService.findById(userId);
        return requestService.getAllByRequester(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAlienRequests(@RequestHeader(Constants.SHARER_USER_ID) Integer userId,
                                                         @PositiveOrZero
                                                         @RequestParam(name = "from", defaultValue = Constants.FROM_DEFAULT)
                                                         int from,
                                                         @Positive
                                                         @RequestParam(name = "size", defaultValue = Constants.SIZE_DEFAULT)
                                                         int size) {
        log.debug("Получен GET запрос к эндпоинту: '/requests/all', Строка параметра запроса для userId={} from={} size={}", userId, from, size);
        return requestService.getAllAlien(userId, from, size).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(@RequestHeader(Constants.SHARER_USER_ID) Integer userId,
                                                 @PathVariable("requestId") Integer requestId) {
        log.debug("Получен GET запрос к эндпоинту: '/requests/{}', Строка параметра запроса для userId={}", requestId, userId);
        userService.findById(userId);
        return RequestMapper.toRequestDto(requestService.getRequestById(requestId));
    }
}