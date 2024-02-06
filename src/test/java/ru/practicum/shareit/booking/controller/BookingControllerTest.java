package ru.practicum.shareit.booking.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import datas.LocalDateAdapter;
import datas.LocalDateTimeAdapter;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static datas.ObjectMaker.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private UserService userService;
    @MockBean
    private ItemService itemService;

    private User booker;
    private Item item;
    private Booking bookingWithoutId;
    private BookingCreateDto bookingDtoWithoutId;
    private Booking booking;
    private BookingCreateDto bookingCreateDto;
    private BookingResponseDto bookingResponseDto;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @BeforeEach
    void setUp() {
        booker = makeUser(1, "Vova", "vova@ya.ru");
        User owner = makeUser(2, "Petr", "petr@ya.ru");
        item = makeItem(1, "item", "description", true,
                owner, null, null);
        bookingWithoutId = makeBooking(null, LocalDateTime.of(2024, 10, 10, 10, 10, 10),
                LocalDateTime.of(2025, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
        bookingDtoWithoutId = makeBookingCreateDto(LocalDateTime.of(2024, 10, 10, 10, 10, 10),
                LocalDateTime.of(2025, 10, 11, 10, 10, 10), item.getId(), booker.getId());
        booking = makeBooking(1, LocalDateTime.of(2024, 10, 10, 10, 10, 10),
                LocalDateTime.of(2025, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
        bookingCreateDto = makeBookingCreateDto(LocalDateTime.of(2024, 10, 10, 10, 10, 10),
                LocalDateTime.of(2025, 10, 11, 10, 10, 10), item.getId(), booker.getId());
        bookingResponseDto = makeBookingResponseDto(1, LocalDateTime.of(2024, 10, 10, 10, 10, 10),
                LocalDateTime.of(2025, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
    }

    @Test
    public void checkSaveBooking() throws Exception {
        when(bookingService.saveBooking(bookingDtoWithoutId, booker.getId())).thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .content(gson.toJson(bookingDtoWithoutId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(gson.toJson(bookingResponseDto)));

        verify(bookingService).saveBooking(bookingDtoWithoutId, booker.getId());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void checkSaveBooking_durationException() throws Exception {
        bookingCreateDto.setEnd(LocalDateTime.of(2021, 10, 11, 10, 10, 10));
        mockMvc.perform(post("/bookings")
                        .content(gson.toJson(bookingCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
        verifyNoInteractions(userService);
        verifyNoInteractions(bookingService);
    }

    @Test
    public void testGetMyBookings() throws Exception {
        int userId = 1;
        String state = "WAITING";
        int from = 0;
        int size = 5;
        List<BookingResponseDto> bookings = List.of();

        when(bookingService.getBookingsByOwnerId(userId, state, from, size)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .header(Constants.SHARER_USER_ID, String.valueOf(userId))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(bookingService).getBookingsByOwnerId(userId, state, from, size);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void checkApproveBooking() throws Exception {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingService.approveBooking(booking.getId(), true, booker.getId()))
                .thenReturn(bookingResponseDto);

        bookingResponseDto.setStatus(BookingStatus.APPROVED);
        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header(USER_ID_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(bookingResponseDto)));

        verify(bookingService).approveBooking(booking.getId(), true, booker.getId());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void checkGetBooking() throws Exception {
        when(bookingService.getBookingById(booking.getId(), booker.getId()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header(USER_ID_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(bookingResponseDto)));

        verify(bookingService).getBookingById(booking.getId(), booker.getId());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void checkGetMyBookingRequests() throws Exception {
        when(bookingService.getBookingRequestsByUserId(booker.getId(), "WAITING", 0, 5))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings", 1L)
                        .header(USER_ID_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(List.of(bookingResponseDto))));

        verify(bookingService).getBookingRequestsByUserId(booker.getId(), "WAITING", 0, 5);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void checkGetMyBookings() throws Exception {
        Integer myId = 2;
        when(bookingService.getBookingRequestsByUserId(myId, "WAITING", 0, 5))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings", 1)
                        .header(USER_ID_HEADER, myId)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(List.of(bookingResponseDto))));

        verify(bookingService).getBookingRequestsByUserId(myId, "WAITING", 0, 5);
        verifyNoMoreInteractions(bookingService);
    }
}