package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.InvalidStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static datas.ObjectMaker.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;

    private User booker;
    private Item item;
    private Booking bookingWithoutId;
    private BookingCreateDto bookingDtoWithoutId;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, userService, itemRepository);
        booker = makeUser(1, "Maria", "maria@ya.ru");
        User owner = makeUser(2, "Oleg", "oleg@ya.ru");
        item = makeItem(1, "item", "description", true, owner, null, null);
        bookingWithoutId = makeBooking(null, LocalDateTime.of(2024, 10, 10, 10, 10, 10),
                LocalDateTime.of(2025, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
        bookingDtoWithoutId = makeBookingCreateDto(LocalDateTime.of(2024, 10, 10, 10, 10, 10),
                LocalDateTime.of(2025, 10, 11, 10, 10, 10), item.getId(), booker.getId());
        booking = makeBooking(1, LocalDateTime.of(2022, 10, 10, 10, 10, 10),
                LocalDateTime.of(2022, 10, 11, 10, 10, 10), BookingStatus.WAITING, item, booker);
    }

    @Test
    public void checkSaveBooking() {
        when(bookingRepository.save(bookingWithoutId)).thenReturn(booking);
        when(itemRepository.findById(1)).thenReturn(Optional.ofNullable(item));
        when(userService.findById(booker.getId())).thenReturn(booker);

        BookingResponseDto savedBooking = bookingService.saveBooking(bookingDtoWithoutId, booker.getId());
        assertEquals(booking.getId(), savedBooking.getId());

        verify(bookingRepository).save(bookingWithoutId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkSaveBooking_unavailableItemException() {
        item.setAvailable(false);
        when(itemRepository.findById(1)).thenReturn(Optional.ofNullable(item));
        when(userService.findById(booker.getId())).thenReturn(booker);

        final var thrown = assertThrows(UnavailableItemException.class,
                () -> bookingService.saveBooking(bookingDtoWithoutId, booker.getId()));
        assertEquals("Item " + 1L + " is unavailable now", thrown.getMessage());

        verifyNoInteractions(bookingRepository);
    }

    @Test
    public void checkSaveBooking_NotFoundItem() {
        final var thrown = assertThrows(NotFoundException.class,
                () -> bookingService.saveBooking(bookingDtoWithoutId, booker.getId()));

        assertEquals("Нет вещи с id = " + booking.getItem().getId(), thrown.getMessage());

        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkApproveBooking() {
        item.setOwner(booker);
        bookingWithoutId.setId(1);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(bookingWithoutId.getId()))
                .thenReturn(Optional.of(bookingWithoutId));

        BookingResponseDto bookingFromDb = bookingService.approveBooking(bookingWithoutId.getId(), true, booker.getId());
        assertEquals(booking.getId(), bookingFromDb.getId());

        verify(bookingRepository).findById(bookingWithoutId.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkApproveBooking_NotFoundBooking() {
        Integer bookingId = 123;
        Boolean isApproved = true;
        Integer ownerId = 1;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.approveBooking(bookingId, isApproved, ownerId);
        });
    }

    @Test
    public void checkApproveBooking_accessException() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        final var thrown = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(booking.getId(), true, booker.getId()));
        assertEquals("Item " + booking.getItem().getId() +
                " from booking " + booking.getId() + " doesn't belong you", thrown.getMessage());

        verify(bookingRepository).findById(booking.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkApproveBooking_invalidStatusException() {
        int ownerId = 1;
        String invalidState = "INVALID_STATE";
        int from = 0;
        int size = 10;

        assertThrows(InvalidStatusException.class, () -> {
            bookingService.getBookingRequestsByUserId(ownerId, invalidState, from, size);
        });
    }

    @Test
    public void checkApproveBooking_invalidStatusExceptionUserId() {
        int ownerId = 1;
        String invalidState = "INVALID_STATE";
        int from = 0;
        int size = 10;

        assertThrows(InvalidStatusException.class, () -> {
            bookingService.getBookingsByOwnerId(ownerId, invalidState, from, size);
        });
    }

    @Test
    public void checkApproveBooking_alreadyApprovedException() {
        item.setOwner(booker);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        final var thrown = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(booking.getId(), true, booker.getId()));
        assertEquals("Booking " + booking.getId() +
                " is already " + BookingStatus.APPROVED, thrown.getMessage());

        verify(bookingRepository).findById(booking.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkGetBookingById() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        BookingResponseDto bookingFromDb = bookingService.getBookingById(booking.getId(), booker.getId());
        assertEquals(booking.getId(), bookingFromDb.getId());

        verify(bookingRepository).findById(booking.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkGetBookingById_noAccessException() {
        Integer alienId = 3;
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        final var thrown = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(booking.getId(), alienId));
        assertEquals("No access to booking " + booking.getId(), thrown.getMessage());

        verify(bookingRepository).findById(booking.getId());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void checkGetBookingById_notFoundException() {
        when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        final var thrown = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1, 1));
        assertEquals("Booking " + 1 + " not found", thrown.getMessage());

        verify(bookingRepository).findById(any());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getBookingsByOwnerId_userNotFound_throwsNotFoundException() {
        int ownerId = 1;
        String state = "ALL";
        int from = 0;
        int size = 10;
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingsByOwnerId(ownerId, state, from, size);
        });
    }
}