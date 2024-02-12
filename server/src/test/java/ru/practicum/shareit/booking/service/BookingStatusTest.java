package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingStatusTest {

    @Test
    public void testApproveRejected() {
        BookingStatus status = BookingStatus.approve(false);
        assertEquals(BookingStatus.REJECTED, status);
    }
}
