package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public Booking saveBooking(Booking booking) {
        if (!booking.getItem().getAvailable().equals(Boolean.TRUE)) {
            throw new UnavailableItemException(booking.getItem().getId());
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveBooking(Integer bookingId, Boolean isApproved, Integer ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Не найден" + bookingId);
        });
        BookingStatus newStatus = BookingStatus.approve(isApproved);
        checkBookingBeforeApprove(booking, newStatus, ownerId);
        booking.setStatus(newStatus);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new BookingNotFoundException(bookingId);
        });
        checkAccess(booking, userId);
        return booking;
    }

    @Override
    public List<Booking> getBookingRequestsByUserId(Integer userId, BookingState state) {
        User booker = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь с не найден userId: " + userId);
        });
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerOrderByStartDesc(booker);
            case CURRENT:
                return bookingRepository.findAllCurrentByBooker(booker);
            case PAST:
                return bookingRepository.findAllPastByBooker(booker);
            case FUTURE:
                return bookingRepository.findAllFutureByBooker(booker);
            case WAITING:
                return bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED);
            default:
                throw new InvalidStatusException();
        }
    }

    @Override
    public List<Booking> getBookingsByOwnerId(Integer ownerId, BookingState state) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден userId: " + ownerId);
        });
        switch (state) {
            case ALL:
                return bookingRepository.findAllByItemOwnerOrderByStartDesc(owner);
            case CURRENT:
                return bookingRepository.findAllCurrentByOwner(owner);
            case PAST:
                return bookingRepository.findAllPastByOwner(owner);
            case FUTURE:
                return bookingRepository.findAllFutureByOwner(owner);
            case WAITING:
                return bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(owner, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(owner, BookingStatus.REJECTED);
            default:
                throw new InvalidStatusException();
        }
    }

    @Override
    public Optional<Booking> getLastBookingByItem(Item item) {
        return bookingRepository.findAllPastOrCurrentByItemDesc(item).stream()
                .findFirst();
    }

    @Override
    public Optional<Booking> getNextBookingByItem(Item item) {
        return bookingRepository.findAllFutureByItemAsc(item).stream()
                .findFirst();
    }

    private void checkBookingBeforeApprove(Booking booking, BookingStatus newStatus, Integer ownerId) {
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Item " + booking.getItem().getId() +
                    " from booking " + booking.getId() + " doesn't belong you");
        }
        if (newStatus.equals(booking.getStatus())) {
            throw new ValidationException("Booking " + booking.getId() +
                    " is already " + newStatus);
        }
    }

    private void checkAccess(Booking booking, Integer userId) {
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingNotFoundException("No access to booking " + booking.getId());
        }
    }
}