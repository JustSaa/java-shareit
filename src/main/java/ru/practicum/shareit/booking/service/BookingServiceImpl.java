package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto saveBooking(BookingCreateDto bookingCreateDto, Integer userId) {
        log.debug("Create Booking: {}, userId: {}", bookingCreateDto, userId);
        validateBookingDuration(bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        Optional<Item> optionalItem = itemRepository.findById(bookingCreateDto.getItemId());
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Нет вещи с id = " + bookingCreateDto.getItemId());
        }

        Item item = optionalItem.get();
        User booker = userService.findById(userId);
        compareBookerAndItemOwner(booker, item);
        Booking booking = BookingMapper.fromBookingCreateDto(bookingCreateDto, item, booker);
        if (!booking.getItem().getAvailable().equals(Boolean.TRUE)) {
            throw new UnavailableItemException(booking.getItem().getId());
        }
        return BookingMapper.toBookingReturnDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto approveBooking(Integer bookingId, Boolean isApproved, Integer ownerId) {
        log.debug("Approve Booking: bookingId: {}, userId: {}, approving: {}", bookingId, ownerId, isApproved);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Не найден" + bookingId);
        });
        BookingStatus newStatus = BookingStatus.approve(isApproved);
        checkBookingBeforeApprove(booking, newStatus, ownerId);
        booking.setStatus(newStatus);
        return BookingMapper.toBookingReturnDto(booking);
    }

    @Override
    public BookingResponseDto getBookingById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Booking " + bookingId + " not found");
        });
        checkAccess(booking, userId);
        return BookingMapper.toBookingReturnDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingRequestsByUserId(Integer userId, String state) {
        BookingState bookingState = BookingState.fromString(state);
        User booker = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь с не найден userId: " + userId);
        });
        List<Booking> bookingResponseDtos;
        switch (bookingState) {
            case ALL:
                bookingResponseDtos = bookingRepository.findAllByBookerOrderByStartDesc(booker);
                break;
            case CURRENT:
                bookingResponseDtos = bookingRepository.findAllCurrentByBooker(booker);
                break;
            case PAST:
                bookingResponseDtos = bookingRepository.findAllPastByBooker(booker);
                break;
            case FUTURE:
                bookingResponseDtos = bookingRepository.findAllFutureByBooker(booker);
                break;
            case WAITING:
                bookingResponseDtos = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingResponseDtos = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED);
                break;
            default:
                throw new InvalidStatusException();
        }
        return bookingResponseDtos.stream()
                .map(BookingMapper::toBookingReturnDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwnerId(Integer ownerId, String state) {
        BookingState bookingState = BookingState.fromString(state);
        User owner = userRepository.findById(ownerId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден userId: " + ownerId);
        });
        List<Booking> bookingResponseDtos;
        switch (bookingState) {
            case ALL:
                bookingResponseDtos = bookingRepository.findAllByItemOwnerOrderByStartDesc(owner);
                break;
            case CURRENT:
                bookingResponseDtos = bookingRepository.findAllCurrentByOwner(owner);
                break;
            case PAST:
                bookingResponseDtos = bookingRepository.findAllPastByOwner(owner);
                break;
            case FUTURE:
                bookingResponseDtos = bookingRepository.findAllFutureByOwner(owner);
                break;
            case WAITING:
                bookingResponseDtos = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(owner, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingResponseDtos = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(owner, BookingStatus.REJECTED);
                break;
            default:
                throw new InvalidStatusException();
        }
        return bookingResponseDtos.stream()
                .map(BookingMapper::toBookingReturnDto)
                .collect(Collectors.toList());
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
            throw new NotFoundException("No access to booking " + booking.getId());
        }
    }

    private void validateBookingDuration(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new ValidationException("Start time (" + start +
                    ") is after then end time (" + end + ")");
        }
    }

    private void compareBookerAndItemOwner(User booker, Item item) {
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Item " + item.getId() + " is your");
        }
    }
}