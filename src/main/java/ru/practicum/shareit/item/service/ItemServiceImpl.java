package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
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
        ItemRequest itemRequest = item.getRequestId() == null
                ? null
                : requestService.getRequestById(item.getRequestId());
        Item itemToDB = ItemMapper.toItem(item, user, itemRequest);
        return itemRepository.save(itemToDB);
    }

    @Transactional
    @Override
    public Item update(ItemDto item, Integer userId, Integer itemId) {
        log.debug("Update ItemDB: {}, userId: {}, itemId: {}", item, userId, itemId);
        userExistenceCheck(userId);
        Optional<Item> existingItem = itemRepository.findById(itemId);
        if (existingItem.isEmpty()) {
            throw new NotFoundException("Предмет с id:" + itemId + " не найден");
        }
        Item updatedItem = existingItem.get();
        item.setId(itemId);
        User owner = userRepository.findById(userId).get();
        Item itemToDB = ItemMapper.toItem(item, owner, null);
        userAuthorizedCheck(itemToDB, userId);
        updateFields(itemToDB, updatedItem);
        return updatedItem;
    }

    @Transactional
    @Override
    public Item findByItemId(Integer itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Нет вещи с id = " + itemId);
        }
        return item.get();
    }

    @Transactional
    @Override
    public List<ItemResponseDto> findAllItems(Integer userId, int from, int size) {
        userExistenceCheck(userId);
        Pageable pageRequest = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.getAllByOwnerIdOrderById(userId, pageRequest).getContent();
        return items.stream()
                .map(item -> ItemMapper.toItemResponseDto(item,
                        bookingService.getLastBookingByItem(item),
                        bookingService.getNextBookingByItem(item)))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemResponseDto findItemByUserIdAndItemId(Integer itemId, Integer userId) {
        userExistenceCheck(userId);
        Item item = findByItemId(itemId);
        return ItemMapper.toItemResponseDto(item,
                item.getOwner().getId().equals(userId) ? bookingService.getLastBookingByItem(item) : null,
                item.getOwner().getId().equals(userId) ? bookingService.getNextBookingByItem(item) : null);
    }

    @Transactional
    @Override
    public void delete(Integer itemId, Integer userId) {
        userAuthorizedCheck(findByItemId(itemId), userId);
        itemRepository.deleteById(itemId);
    }

    @Transactional
    public List<ItemDto> searchItem(Integer userId, String text, int from, int size) {
        userExistenceCheck(userId);
        Pageable pageRequest = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.getAllByTemplate(text, pageRequest).getContent();
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(ItemMapper::toItemDto)
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

        if (item.getName() != null && !item.getName().isBlank()) {
            itemToUpdate.setName(item.getName());
        }

        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemToUpdate.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
    }

    @Transactional
    @Override
    public Comment saveComment(Integer itemId, CommentCreateDto commentCreateDto, Integer userId) {
        log.debug("Create Comment: {}, itemId: {}, userId: {}", commentCreateDto, itemId, userId);
        userExistenceCheck(userId);
        User author = userRepository.findById(userId).get();
        Item item = findByItemId(itemId);
        Comment comment = ItemMapper.toComment(commentCreateDto, author, item);
        List<Booking> userBookings = bookingRepository.findAllPastByBooker(comment.getAuthor());
        boolean doesAuthorRentThisItem = userBookings.stream().anyMatch(booking ->
                booking.getItem().getId().equals(comment.getItem().getId()));
        if (!doesAuthorRentThisItem) {
            throw new UnavailableItemException(comment.getItem().getId());
        }
        return commentRepository.save(comment);
    }
}
