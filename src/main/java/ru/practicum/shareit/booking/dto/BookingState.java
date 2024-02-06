package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.InvalidStatusException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState fromString(String stringStatus) {
        try {
            return BookingState.valueOf(stringStatus);
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException("Unknown state: " + stringStatus);
        }
    }
}
