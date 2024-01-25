package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking saveBooking(Booking booking);

    Booking approveBooking(Integer bookingId, Boolean isApproved, Integer ownerId);

    Booking getBookingById(Integer bookingId, Integer userId);

    List<Booking> getBookingRequestsByUserId(Integer userId, BookingState state);

    List<Booking> getBookingsByOwnerId(Integer ownerId, BookingState state);

    Optional<Booking> getLastBookingByItem(Item item);

    Optional<Booking> getNextBookingByItem(Item item);
}
