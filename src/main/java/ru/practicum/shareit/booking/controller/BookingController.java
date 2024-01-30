package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constants.Constants;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto saveBooking(@Valid @RequestBody BookingCreateDto bookingCreateDto,
                                          @RequestHeader(Constants.SHARER_USER_ID) Integer userId) {
        log.info("Получен POST запрос к эндпоинту: '/bookings', Строка параметра запроса для booking: {}", bookingCreateDto);
        return bookingService.saveBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto approveBooking(@PathVariable("bookingId") Integer bookingId,
                                             @RequestParam("approved") Boolean approved,
                                             @RequestHeader(Constants.SHARER_USER_ID) Integer userId) {
        log.info("Получен PATCH запрос к эндпоинту: '/bookings" +
                " Строка параметра запроса для bookingId: {} к approved: {}", bookingId, approved);
        return bookingService.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getBooking(@PathVariable("bookingId") Integer bookingId,
                                         @RequestHeader(Constants.SHARER_USER_ID) Integer userId) {
        log.info("Получен GET запрос к эндпоинту: '/bookings" +
                " Строка параметра запроса для bookingId: {} и userId: {}", bookingId, userId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getMyBookingRequests(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                         @RequestHeader(Constants.SHARER_USER_ID) Integer userId) {
        log.info("Получен GET запрос к эндпоинту: '/bookings" +
                " Строка параметра запроса для state: {} и userId: {}", state, userId);
        return bookingService.getBookingRequestsByUserId(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getMyBookings(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                  @RequestHeader(Constants.SHARER_USER_ID) Integer userId) {
        log.info("Получен GET запрос к эндпоинту: '/bookings" +
                " Строка параметра запроса для state: {} и userId: {}", state, userId);
        return bookingService.getBookingsByOwnerId(userId, state);
    }
}