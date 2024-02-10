package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingService {
    BookingResponseDto saveBooking(BookingCreateDto bookingCreateDto, Integer userId);

    BookingResponseDto approveBooking(Integer bookingId, Boolean isApproved, Integer ownerId);

    BookingResponseDto getBookingById(Integer bookingId, Integer userId);

    List<BookingResponseDto> getBookingRequestsByUserId(Integer userId, String state, int from, int size);

    List<BookingResponseDto> getBookingsByOwnerId(Integer ownerId, String state, int from, int size);

    Booking getLastBookingByItem(Item item);

    Booking getNextBookingByItem(Item item);
}
