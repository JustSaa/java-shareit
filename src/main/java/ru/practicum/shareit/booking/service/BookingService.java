package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingResponseDto saveBooking(BookingCreateDto bookingCreateDto, Integer userId);

    BookingResponseDto approveBooking(Integer bookingId, Boolean isApproved, Integer ownerId);

    BookingResponseDto getBookingById(Integer bookingId, Integer userId);

    List<BookingResponseDto> getBookingRequestsByUserId(Integer userId, String state);

    List<BookingResponseDto> getBookingsByOwnerId(Integer ownerId, String state);

    Optional<Booking> getLastBookingByItem(Item item);

    Optional<Booking> getNextBookingByItem(Item item);
}
