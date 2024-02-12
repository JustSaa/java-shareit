package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {
    ItemRequest saveRequest(Integer userId, ItemRequestCreateDto itemRequest);

    List<ItemRequest> getAllByRequester(Integer userId);

    List<ItemRequest> getAllAlien(Integer userId, int from, int size);

    ItemRequest getRequestById(Integer requestId);
}