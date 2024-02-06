package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {
    public ItemRequest toRequest(ItemRequestCreateDto itemRequestCreateDto, User register) {
        return ItemRequest.builder()
                .description(itemRequestCreateDto.getDescription())
                .created(LocalDateTime.now())
                .requester(register)
                .build();
    }

    public ItemRequestResponseDto toRequestDto(ItemRequest itemRequest) {
        List<ItemResponseDto> items = null;
        if (itemRequest.getItems() != null) {
            items = itemRequest.getItems().stream()
                    .map(item -> ItemMapper.toItemResponseDto(item, null, null))
                    .collect(Collectors.toList());
        }
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(UserMapper.toUserDto(itemRequest.getRequester()))
                .items(items)
                .build();
    }
}