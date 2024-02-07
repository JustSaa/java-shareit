package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.custom.CustomPageRequest;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingResponseDto saveBooking(BookingCreateDto bookingCreateDto, Integer userId) {
        log.debug("Create Booking: {}, userId: {}", bookingCreateDto, userId);
        validateBookingDuration(bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        Item optionalItem = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Нет вещи с id = " + bookingCreateDto.getItemId()));
        User booker = userService.findById(userId);
        compareBookerAndItemOwner(booker, optionalItem);
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingCreateDto, optionalItem, booker);
        if (!booking.getItem().getAvailable().equals(Boolean.TRUE)) {
            throw new UnavailableItemException(booking.getItem().getId());
        }
        return BookingMapper.INSTANCE.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingResponseDto approveBooking(Integer bookingId, Boolean isApproved, Integer ownerId) {
        log.debug("Approve Booking: bookingId: {}, userId: {}, approving: {}", bookingId, ownerId, isApproved);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Не найден" + bookingId);
        });
        BookingStatus newStatus = BookingStatus.approve(isApproved);
        checkBookingBeforeApprove(booking, newStatus, ownerId);
        booking.setStatus(newStatus);
        return BookingMapper.INSTANCE.toBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto getBookingById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Booking " + bookingId + " not found");
        });
        checkAccess(booking, userId);
        return BookingMapper.INSTANCE.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingRequestsByUserId(Integer userId, String state, int from, int size) {
        BookingState bookingState = BookingState.fromString(state);
        User booker = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь с не найден userId: " + userId);
        });
        Pageable pageRequest = new CustomPageRequest(from, size);
        List<Booking> bookingResponseDtos;
        log.debug("Get Booking by User request: state: {}, userId: {}", bookingState, userId);
        switch (bookingState) {
            case ALL:
                bookingResponseDtos = bookingRepository.findAllByBookerOrderByStartDesc(booker, pageRequest).getContent();
                break;
            case CURRENT:
                bookingResponseDtos = bookingRepository.findAllCurrentByBooker(booker, pageRequest).getContent();
                break;
            case PAST:
                bookingResponseDtos = bookingRepository.findAllPastByBooker(booker, pageRequest).getContent();
                break;
            case FUTURE:
                bookingResponseDtos = bookingRepository.findAllFutureByBooker(booker, pageRequest).getContent();
                break;
            case WAITING:
                bookingResponseDtos = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker,
                        BookingStatus.WAITING, pageRequest).getContent();
                break;
            case REJECTED:
                bookingResponseDtos = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker,
                        BookingStatus.REJECTED, pageRequest).getContent();
                break;
            default:
                throw new InvalidStatusException();
        }
        return bookingResponseDtos.stream()
                .map(BookingMapper.INSTANCE::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwnerId(Integer ownerId, String state, int from, int size) {
        BookingState bookingState = BookingState.fromString(state);
        User owner = userRepository.findById(ownerId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден userId: " + ownerId);
        });
        Pageable pageRequest = new CustomPageRequest(from, size);
        List<Booking> bookingResponseDtos;
        switch (bookingState) {
            case ALL:
                bookingResponseDtos = bookingRepository.findAllByItemOwnerOrderByStartDesc(owner, pageRequest).getContent();
                break;
            case CURRENT:
                bookingResponseDtos = bookingRepository.findAllCurrentByOwner(owner, pageRequest).getContent();
                break;
            case PAST:
                bookingResponseDtos = bookingRepository.findAllPastByOwner(owner, pageRequest).getContent();
                break;
            case FUTURE:
                bookingResponseDtos = bookingRepository.findAllFutureByOwner(owner, pageRequest).getContent();
                break;
            case WAITING:
                bookingResponseDtos = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(owner,
                        BookingStatus.WAITING, pageRequest).getContent();
                break;
            case REJECTED:
                bookingResponseDtos = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(owner,
                        BookingStatus.REJECTED, pageRequest).getContent();
                break;
            default:
                throw new InvalidStatusException();
        }
        return bookingResponseDtos.stream()
                .map(BookingMapper.INSTANCE::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Booking getLastBookingByItem(Item item) {
        return bookingRepository.findAllPastOrCurrentByItemDesc(item, new CustomPageRequest(0, 1)).stream()
                .findFirst().orElse(null);
    }

    @Override
    public Booking getNextBookingByItem(Item item) {
        return bookingRepository.findAllFutureByItemAsc(item, new CustomPageRequest(0, 1)).stream()
                .findFirst().orElse(null);
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