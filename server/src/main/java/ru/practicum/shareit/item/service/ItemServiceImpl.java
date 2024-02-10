package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.CustomPageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final RequestService requestService;

    @Transactional
    @Override
    public Item create(ItemDto item, Integer userId) {
        log.debug("Create ItemDB: {}, userId: {}", item, userId);
        userExistenceCheck(userId);
        User user = userRepository.findById(userId).get();
        ItemRequest itemRequest = Optional.ofNullable(item.getRequestId())
                .map(requestService::getRequestById)
                .orElse(null);
        Item itemToDB = ItemMapper.INSTANCE.toItem(item, user, itemRequest);
        return itemRepository.save(itemToDB);
    }

    @Transactional
    @Override
    public Item update(ItemDto item, Integer userId, Integer itemId) {
        log.debug("Update ItemDB: {}, userId: {}, itemId: {}", item, userId, itemId);
        userExistenceCheck(userId);
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id:" + itemId + " не найден"));
        item.setId(itemId);
        User owner = userRepository.findById(userId).get();
        Item itemToDB = ItemMapper.INSTANCE.toItem(item, owner, null);
        userAuthorizedCheck(itemToDB, userId);
        updateFields(itemToDB, existingItem);
        return existingItem;
    }

    @Override
    public List<ItemResponseDto> findAllItems(Integer userId, int from, int size) {
        userExistenceCheck(userId);
        Pageable pageRequest = new CustomPageRequest(from, size);
        List<Item> items = itemRepository.getAllByOwnerIdOrderById(userId, pageRequest).getContent();
        return items.stream()
                .map(item -> ItemMapper.INSTANCE.toItemResponseDto(item,
                        bookingService.getLastBookingByItem(item),
                        bookingService.getNextBookingByItem(item)))
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto findItemByUserIdAndItemId(Integer itemId, Integer userId) {
        userExistenceCheck(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Нет вещи с id = " + itemId));
        return ItemMapper.INSTANCE.toItemResponseDto(item,
                item.getOwner().getId().equals(userId) ? bookingService.getLastBookingByItem(item) : null,
                item.getOwner().getId().equals(userId) ? bookingService.getNextBookingByItem(item) : null);
    }

    @Transactional
    @Override
    public void delete(Integer itemId, Integer userId) {
        userAuthorizedCheck(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Нет вещи с id = " + itemId)), userId);
        itemRepository.deleteById(itemId);
    }

    public List<ItemDto> searchItem(Integer userId, String text, int from, int size) {
        userExistenceCheck(userId);
        Pageable pageRequest = new CustomPageRequest(from, size);
        List<Item> items = itemRepository.getAllByTemplate(text, pageRequest).getContent();
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(ItemMapper.INSTANCE::toItemDto)
                .collect(Collectors.toList());
    }

    private void userExistenceCheck(Integer userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Нет пользователя с id =" + userId);
        }
    }

    private void userAuthorizedCheck(Item item, Integer userId) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Пользователь с id:"
                    + userId + " не имеет права редактировать предмет");
        }
    }

    private void updateFields(Item item, Item itemToUpdate) {
        Optional.ofNullable(item.getName())
                .filter(name -> !name.isBlank())
                .ifPresent(itemToUpdate::setName);

        Optional.ofNullable(item.getDescription())
                .filter(description -> !description.isBlank())
                .ifPresent(itemToUpdate::setDescription);

        Optional.ofNullable(item.getAvailable())
                .ifPresent(itemToUpdate::setAvailable);
    }

    @Transactional
    @Override
    public Comment saveComment(Integer itemId, CommentCreateDto commentCreateDto, Integer userId) {
        log.debug("Create Comment: {}, itemId: {}, userId: {}", commentCreateDto, itemId, userId);
        userExistenceCheck(userId);
        User author = userRepository.findById(userId).get();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Нет вещи с id = " + itemId));
        Comment comment = ItemMapper.INSTANCE.toComment(commentCreateDto, author, item);
        List<Booking> userBookings = bookingRepository.findAllPastByBooker(comment.getAuthor());
        boolean doesAuthorRentThisItem = userBookings.stream().anyMatch(booking ->
                booking.getItem().getId().equals(comment.getItem().getId()));
        if (!doesAuthorRentThisItem) {
            throw new UnavailableItemException(comment.getItem().getId());
        }
        return commentRepository.save(comment);
    }
}
