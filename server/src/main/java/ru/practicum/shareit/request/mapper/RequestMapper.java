package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(imports = {LocalDateTime.class, UUID.class, UserMapper.class, ru.practicum.shareit.item.mapper.ItemMapper.class})
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(target = "description", source = "itemRequestCreateDto.description")
    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    @Mapping(target = "requester", source = "register")
    ItemRequest toRequest(ItemRequestCreateDto itemRequestCreateDto, User register);

    @Mapping(target = "requester", expression = "java(UserMapper.INSTANCE.toUserDto(itemRequest.getRequester()))")
    @Mapping(target = "items", expression = "java(getItemResponse(itemRequest))")
    ItemRequestResponseDto toRequestDto(ItemRequest itemRequest);


    default List<ItemResponseDto> getItemResponse(ItemRequest itemRequest) {
        List<ItemResponseDto> items = null;
        if (itemRequest.getItems() != null) {
            items = itemRequest.getItems().stream()
                    .map(item -> ItemMapper.INSTANCE.toItemResponseDto(item, null, null))
                    .collect(Collectors.toList());
        }
        return items;
    }
}