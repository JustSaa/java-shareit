package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMapperTest {

    private final RequestMapper mapper = Mappers.getMapper(RequestMapper.class);

    @Test
    void testToRequest() {
        ItemRequestCreateDto requestCreateDto = new ItemRequestCreateDto();
        requestCreateDto.setDescription("Test description");

        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");

        ItemRequest itemRequest = mapper.toRequest(requestCreateDto, user);

        assertEquals("Test description", itemRequest.getDescription());
        assertEquals(user, itemRequest.getRequester());
    }

    @Test
    void testToRequestDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Test description");

        List<ItemResponseDto> items = Collections.emptyList();

        ItemRequestResponseDto responseDto = mapper.toRequestDto(itemRequest);

        assertEquals(itemRequest.getId(), responseDto.getId());
        assertEquals(itemRequest.getDescription(), responseDto.getDescription());
    }
}