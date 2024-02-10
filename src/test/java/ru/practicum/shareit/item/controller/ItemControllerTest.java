package ru.practicum.shareit.item.controller;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import datas.LocalDateAdapter;
import datas.LocalDateTimeAdapter;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static datas.ObjectMaker.*;
import static datas.ObjectMaker.makeBookingResponseDto;

@Slf4j
@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private RequestService requestService;

    private User user;
    private Item itemWithoutId;
    private Item item;
    private ItemDto itemDtoWithoutId;
    private ItemDto itemDto;
    private ItemResponseDto itemResponseDto;

    @BeforeEach
    void setUp() {
        user = makeUser(2, "Igor", "igor@ya.ru");
        itemWithoutId = makeItem(null, "item1", "description1", true, user,
                Collections.emptyList(), null);
        item = makeItem(1, "item1", "description1", true, user,
                Collections.emptyList(), null);
        itemDtoWithoutId = makeItemDto(null, "item1", "description1", true, 1);
        itemDto = makeItemDto(1, "item1", "description1", true, null);
        itemResponseDto = makeItemResponseDto(1, "item1", "description1", true,
                1, null, null, Collections.emptyList());
    }

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @Test
    public void checkSaveItemWithoutRequest() throws Exception {
        when(itemService.create(itemDtoWithoutId, user.getId())).thenReturn(item);

        mockMvc.perform(post("/items")
                        .content(gson.toJson(itemDtoWithoutId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json((gson.toJson(itemDto))));

        verify(itemService).create(itemDtoWithoutId, user.getId());
        verifyNoMoreInteractions(itemService);
        verifyNoInteractions(requestService);
    }

    @Test
    public void checkSaveItemWithRequest() throws Exception {
        User requester = makeUser(3, "Nick", "nick@ya.ru");
        ItemRequest itemRequest = makeRequest(3, "some request",
                LocalDateTime.of(2022, 9, 11, 10, 10, 10), requester, null);

        itemWithoutId.setItemRequest(itemRequest);
        item.setItemRequest(itemRequest);
        when(itemService.create(itemDtoWithoutId, user.getId())).thenReturn(item);

        itemDtoWithoutId.setRequestId(itemRequest.getId());
        itemDto.setRequestId(itemRequest.getId());

        mockMvc.perform(post("/items")
                        .content(gson.toJson(itemDtoWithoutId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json((gson.toJson(itemDto))));

        verify(itemService).create(itemDtoWithoutId, user.getId());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void checkUpdateItem() throws Exception {
        when(itemService.update(itemDtoWithoutId, user.getId(), 1)).thenReturn(item);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(itemDtoWithoutId)))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(itemDto)));

        verify(itemService).update(itemDtoWithoutId, user.getId(), 1);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void checkGetItem() throws Exception {
        when(itemService.findItemByUserIdAndItemId(item.getId(), user.getId())).thenReturn(itemResponseDto);

        ItemResponseDto itemDto = makeItemResponseDto(1, "item1", "description1", true,
                1, null, null, Collections.emptyList());
        mockMvc.perform(get("/items/{itemId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(itemDto)));

        verifyNoInteractions(bookingService);
    }

    @Test
    public void checkGetItemWithBookings() throws Exception {
        BookingDto lastBooking = makeBookingDto(4, LocalDateTime.of(2022, 9, 11, 10, 10, 10),
                LocalDateTime.of(2022, 9, 11, 20, 10, 10), 2);
        BookingDto nextBooking = makeBookingDto(4, LocalDateTime.of(2022, 10, 11, 10, 10, 10),
                LocalDateTime.of(2022, 10, 11, 20, 10, 10), 2);
        itemResponseDto.setNextBooking(nextBooking);
        itemResponseDto.setLastBooking(lastBooking);
        when(itemService.findItemByUserIdAndItemId(item.getId(), user.getId())).thenReturn(itemResponseDto);

        BookingResponseDto lastBookingDto = makeBookingResponseDto(4, LocalDateTime.of(2022, 9, 11, 10, 10, 10),
                LocalDateTime.of(2022, 9, 11, 20, 10, 10), BookingStatus.APPROVED, item, user);
        BookingResponseDto nextBookingDto = makeBookingResponseDto(4, LocalDateTime.of(2022, 10, 11, 10, 10, 10),
                LocalDateTime.of(2022, 10, 11, 20, 10, 10), BookingStatus.WAITING, item, user);
        ItemResponseDto itemDto = makeItemResponseDto(1, "item1", "description1",
                true, null, lastBookingDto, nextBookingDto, Collections.emptyList());

        mockMvc.perform(get("/items/{itemId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(itemDto)));

        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void checkGetMyItems() throws Exception {
        when(itemService.findAllItems(user.getId(), 0, 2)).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId())
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(List.of(itemResponseDto))));

        verify(itemService).findAllItems(user.getId(), 0, 2);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void checkSearchEmptyList() throws Exception {
        mockMvc.perform(get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId())
                        .param("text", "")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(Collections.emptyList())));
    }

    @Test
    public void checkSearch() throws Exception {
        String template = "item";
        when(itemService.searchItem(user.getId(), template, 0, 2)).thenReturn(List.of(itemDtoWithoutId));

        mockMvc.perform(get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId())
                        .param("text", template)
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(List.of(itemDtoWithoutId))));

        verify(itemService).searchItem(user.getId(), template, 0, 2);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void checkSaveComment() throws Exception {
        CommentCreateDto commentBeforeSave = makeCreateComment("comment");
        Comment commentAfterSave = makeComment(1L, "comment", item, user, LocalDateTime.of(2024, 2, 5, 20, 1, 16, 130313000));
        when(itemService.saveComment(item.getId(), commentBeforeSave, user.getId())).thenReturn(commentAfterSave);

        CommentResponseDto commentResponseDto = makeCommentResponseDto(1L, "comment",
                "item1", "Igor", LocalDateTime.of(2024, 2, 5, 20, 1, 16, 130313000));

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(new CommentCreateDto("comment"))))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(commentResponseDto)));

        verify(itemService).saveComment(item.getId(), commentBeforeSave, user.getId());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void deleteItemSuccess() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", 1)
                        .header(USER_ID_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService).delete(1, 1);
    }
}