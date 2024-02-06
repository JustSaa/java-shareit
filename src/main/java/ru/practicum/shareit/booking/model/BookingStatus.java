package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.InvalidStatusException;

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    public static BookingStatus fromString(String stringStatus) {
        try {
            return BookingStatus.valueOf(stringStatus);
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException("Unknown state: " + stringStatus);
        }
    }

    public static BookingStatus approve(boolean isApproved) {
        return isApproved ? APPROVED : REJECTED;
    }
}
