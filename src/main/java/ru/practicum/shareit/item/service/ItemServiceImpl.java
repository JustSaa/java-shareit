package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    @Override
    public Item create(Item item, Integer userId) {
        log.debug("Create ItemDB: {}, userId: {}", item, userId);
        userExistenceCheck(userId);
        User user = userRepository.findById(userId).get();
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item update(Item item, Integer userId, Integer itemId) {
        log.debug("Update ItemDB: {}, userId: {}, itemId: {}", item, userId, itemId);
        userExistenceCheck(userId);
        Optional<Item> existingItem = itemRepository.findById(itemId);
        if (existingItem.isEmpty()) {
            throw new NotFoundException("Предмет с id:" + itemId + " не найден");
        }

        Item updatedItem = existingItem.get();
        userAuthorizedCheck(updatedItem, userId);
        updateFields(item, updatedItem);
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
    public List<ItemResponseDto> findAllItems(Integer userId) {
        userExistenceCheck(userId);
        return itemRepository.getAllByOwnerIdOrderById(userId).stream()
                .map(item -> ItemMapper.toItemReturnDto(item,
                        bookingService.getLastBookingByItem(item),
                        bookingService.getNextBookingByItem(item)))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemResponseDto findItemByUserIdAndItemId(Integer itemId, Integer userId) {
        userExistenceCheck(userId);
        Item item = findByItemId(itemId);
        if (item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemReturnDto(item,
                    bookingService.getLastBookingByItem(item),
                    bookingService.getNextBookingByItem(item));
        } else {
            return ItemMapper.toItemReturnDto(item,
                    Optional.empty(),
                    Optional.empty());
        }
    }

    @Transactional
    @Override
    public void delete(Integer itemId, Integer userId) {
        userAuthorizedCheck(findByItemId(itemId), userId);
        itemRepository.deleteById(itemId);
    }

    @Transactional
    public List<ItemDto> searchItem(Integer userId, String text) {
        userExistenceCheck(userId);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.getAllByTemplate(text).stream()
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
