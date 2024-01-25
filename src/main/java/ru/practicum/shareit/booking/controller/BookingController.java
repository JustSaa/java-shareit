package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public BookingResponseDto saveBooking(@Valid @RequestBody BookingCreateDto bookingCreateDto,
                                        @RequestHeader(SHARER_USER_ID) Integer userId) {
        validateBookingDuration(bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        Item item = itemService.findByItemId(bookingCreateDto.getItemId());
        User booker = userService.findById(userId);
        compareBookerAndItemOwner(booker, item);
        Booking booking = BookingMapper.fromBookingCreateDto(bookingCreateDto, item, booker);
        return BookingMapper.toBookingReturnDto(
                bookingService.saveBooking(booking));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable("bookingId") Integer bookingId,
                                             @RequestParam("approved") Boolean approved,
                                             @RequestHeader(SHARER_USER_ID) Integer userId) {
        return BookingMapper.toBookingReturnDto(
                bookingService.approveBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable("bookingId") Integer bookingId,
                                         @RequestHeader(SHARER_USER_ID) Integer userId) {
        return BookingMapper.toBookingReturnDto(
                bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingResponseDto> getMyBookingRequests(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                         @RequestHeader(SHARER_USER_ID) Integer userId) {
        BookingState bookingState = BookingState.fromString(state);
        return bookingService.getBookingRequestsByUserId(userId, bookingState).stream()
                .map(BookingMapper::toBookingReturnDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getMyBookings(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                  @RequestHeader(SHARER_USER_ID) Integer userId) {
        BookingState bookingState = BookingState.fromString(state);
        return bookingService.getBookingsByOwnerId(userId, bookingState).stream()
                .map(BookingMapper::toBookingReturnDto)
                .collect(Collectors.toList());
    }

    private void validateBookingDuration(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new ValidationException("Start time (" + start +
                    ") is after then end time (" + end + ")");
        }
    }

    private void compareBookerAndItemOwner(User booker, Item item) {
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new BookingNotFoundException("Item " + item.getId() + " is your");
        }
    }
}