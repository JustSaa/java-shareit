package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public Booking fromBookingCreateDto(BookingCreateDto bookingCreateDto, Item item, User booker) {
        return new Booking(bookingCreateDto.getId(),
                bookingCreateDto.getStart(),
                bookingCreateDto.getEnd(),
                item,
                booker,
                BookingStatus.WAITING);
    }

    public BookingCreateDto toBookingCreateDto(Booking booking) {
        return new BookingCreateDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId());
    }

    public BookingResponseDto toBookingReturnDto(Booking booking) {
        Item item = booking.getItem();
        User booker = booking.getBooker();
        return new BookingResponseDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new BookingResponseDto.ItemDto(item.getId(), item.getName()),
                new UserDto(booker.getId()),
                booking.getStatus());
    }
}
