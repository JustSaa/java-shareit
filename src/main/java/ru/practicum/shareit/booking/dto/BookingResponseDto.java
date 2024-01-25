package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingResponseDto {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private BookingStatus status;

    @Data
    @AllArgsConstructor
    public static class ItemDto {
        private Integer id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class UserDto {
        private Integer id;
    }
}
