package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public Booking toBooking(BookingCreateDto bookingCreateDto, Item item, User booker) {
        return Booking.builder()
                .id(bookingCreateDto.getId())
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public BookingCreateDto toBookingCreateDto(Booking booking) {
        return BookingCreateDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .userId(booking.getBooker().getId())
                .build();
    }

    public BookingResponseDto toBookingResponseDto(Booking booking) {
        Item item = booking.getItem();
        User booker = booking.getBooker();
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(new ItemDto(item.getId(), item.getName()))
                .booker(new UserDto(booker.getId()))
                .status(booking.getStatus())
                .build();
    }
}
