package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking fromBookingCreateDto(BookingCreateDto bookingCreateDto, Item item, User booker) {
        return new Booking(bookingCreateDto.getId(),
                bookingCreateDto.getStart(),
                bookingCreateDto.getEnd(),
                item,
                booker,
                BookingStatus.WAITING);
    }

    public static BookingCreateDto toBookingCreateDto(Booking booking) {
        return new BookingCreateDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId());
    }

    public static BookingResponseDto toBookingReturnDto(Booking booking) {
        Item item = booking.getItem();
        User booker = booking.getBooker();
        return new BookingResponseDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new BookingResponseDto.ItemDto(item.getId(), item.getName()),
                new BookingResponseDto.UserDto(booker.getId()),
                booking.getStatus());
    }
}
