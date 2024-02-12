package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    public static BookingStatus approve(boolean isApproved) {
        return isApproved ? APPROVED : REJECTED;
    }
}
