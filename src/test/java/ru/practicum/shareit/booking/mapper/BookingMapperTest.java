package ru.practicum.shareit.booking.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapperTest {

    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void testToBooking() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setId(1);

        Item item = new Item();
        item.setId(2);

        User user = new User();
        user.setId(3);

        Booking booking = mapper.toBooking(createDto, item, user);

        assertEquals(1, booking.getId());
        assertEquals(2, booking.getItem().getId());
        assertEquals(3, booking.getBooker().getId());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void testToBookingResponseDto() {
        Booking booking = new Booking();
        booking.setId(1);

        BookingResponseDto responseDto = mapper.toBookingResponseDto(booking);

        assertEquals(1, responseDto.getId());
    }
}
