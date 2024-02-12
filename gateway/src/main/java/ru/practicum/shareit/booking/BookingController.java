package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

import static ru.practicum.shareit.constants.Constants.*;

@Slf4j
@Validated
@RestController
@RequestMapping(path = BOOKING_API_PREFIX)
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveBooking(@Valid @RequestBody BookingCreateDto bookingCreateDto,
                                              @RequestHeader(SHARER_USER_ID) Integer userId) {
        validateBookingDuration(bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        log.debug("Creating booking: userId={}, body: {}", userId, bookingCreateDto);
        return bookingClient.saveBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> approveBooking(@PathVariable("bookingId") Integer bookingId,
                                                 @RequestParam("approved") Boolean approved,
                                                 @RequestHeader(SHARER_USER_ID) Integer userId) {
        log.debug("Approving booking {}: userId={}, approved={}}", bookingId, userId, approved);
        return bookingClient.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBooking(@PathVariable("bookingId") Integer bookingId,
                                             @RequestHeader(SHARER_USER_ID) Integer userId) {
        log.debug("Get booking {}: userId={}}", bookingId, userId);
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getMyBookingRequests(@RequestParam(name = "state", defaultValue = STATE_DEFAULT)
                                                       String state,
                                                       @RequestHeader(SHARER_USER_ID) Integer userId,
                                                       @PositiveOrZero
                                                       @RequestParam(name = "from", defaultValue = FROM_DEFAULT)
                                                       Integer from,
                                                       @Positive
                                                       @RequestParam(name = "size", defaultValue = SIZE_DEFAULT)
                                                       Integer size) {
        BookingState bookingState = BookingState.fromString(state);
        log.debug("Get bookings that user {} booked: state={}, from={}, size={}", userId, state, from, size);
        return bookingClient.getMyBookingRequests(bookingState, userId, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getMyBookings(@RequestParam(name = "state", defaultValue = STATE_DEFAULT)
                                                String state,
                                                @RequestHeader(SHARER_USER_ID) Integer userId,
                                                @PositiveOrZero
                                                @RequestParam(name = "from", defaultValue = FROM_DEFAULT) Integer from,
                                                @Positive
                                                @RequestParam(name = "size", defaultValue = SIZE_DEFAULT) Integer size) {
        BookingState bookingState = BookingState.fromString(state);
        log.debug("Get bookings that user {} owns: state={}, from={}, size={}", userId, state, from, size);
        return bookingClient.getMyBookings(bookingState, userId, from, size);
    }

    private void validateBookingDuration(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new ValidationException("Start time (" + start +
                    ") is after then end time (" + end + ")");
        }
    }
}