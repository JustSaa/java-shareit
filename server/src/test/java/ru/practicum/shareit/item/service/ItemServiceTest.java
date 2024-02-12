package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static datas.ObjectMaker.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    private ItemService itemService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingService bookingService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestService requestService;

    private Item item;
    private Item item2;
    private ItemDto item3;
    private Item itemWithoutId;
    private Item itemWithoutIdUser2;
    private ItemDto itemWithoutIdDto;
    private User userForTest;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(bookingRepository, itemRepository, userRepository, bookingService, commentRepository, requestService);
        User user = makeUser(1, "Dmitry", "dmitry@ya.ru");
        userForTest = makeUser(2, "Ivan", "ivan@ya.ru");
        itemWithoutId = makeItem(null, "item1", "description1", true,
                user, null, null);
        itemWithoutIdUser2 = makeItem(null, "item1", "description1", true,
                userForTest, null, null);
        itemWithoutIdDto = makeItemDto(null, "item1", "description1", true,
                1);
        item = makeItem(1, "item1", "description1", true,
                user, new ArrayList<>(), null);
        item2 = makeItem(1, "item1", "description1", true,
                userForTest, null, null);
        item3 = makeItemDto(1, "item1", "description1", true,
                1);
    }

    @Test
    public void checkSaveItem() {
        when(itemRepository.save(itemWithoutIdUser2)).thenReturn(item2);
        when(userRepository.findById(2)).thenReturn(Optional.ofNullable(userForTest));

        Item savedItem = itemService.create(itemWithoutIdDto, userForTest.getId());

        assertEquals(item2, savedItem);
        verify(itemRepository).save(itemWithoutIdUser2);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void checkUpdateItem() {
        item2.setName("updated");
        item2.setDescription("updated");
        when(userRepository.findById(2)).thenReturn(Optional.ofNullable(userForTest));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item2));

        Item updatedItem = itemService.update(item3, 2, 1);

        assertEquals(item2, updatedItem);
        verify(userRepository, times(2)).findById(2);
        verify(itemRepository).findById(item.getId());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void checkUpdateItemItemNoFoundException() {
        final var thrown = assertThrows(NotFoundException.class, () -> itemService.update(itemWithoutIdDto, 1, 1));
        assertEquals("Нет пользователя с id =" + item.getId(), thrown.getMessage());
    }

    @Test
    public void checkSaveComment() {
        User booker = makeUser(1, "booker", "user@ya.ru");
        CommentCreateDto commentDtoBeforeSave = makeCreateComment("comment");
        Comment commentAfterSave = makeComment(1L, "comment", item, booker, LocalDateTime.of(2024, 2, 6, 12, 30, 45));
        Booking booking = makeBooking(1, LocalDateTime.of(2022, 10, 10, 12, 12, 12),
                LocalDateTime.of(2022, 10, 10, 12, 12, 12),
                BookingStatus.APPROVED, item, booker);
        when(bookingRepository.findAllPastByBooker(booker)).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(commentAfterSave);
        when(userRepository.findById(1)).thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.ofNullable(item));

        Comment savedComment = itemService.saveComment(1, commentDtoBeforeSave, 1);
        assertEquals(commentAfterSave.getId(), savedComment.getId());

        verify(bookingRepository).findAllPastByBooker(booker);
        verify(commentRepository).save(any());
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    public void checkSaveCommentAuthorWasNotBooker() {
        User booker = makeUser(1, "booker", "user@ya.ru");
        Comment commentBeforeSave = makeComment(null, "comment", item, booker, LocalDateTime.now());
        CommentCreateDto commentDtoBeforeSave = makeCreateComment("comment");
        when(bookingRepository.findAllPastByBooker(booker)).thenReturn(Collections.emptyList());
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        final var thrown = assertThrows(UnavailableItemException.class,
                () -> itemService.saveComment(1, commentDtoBeforeSave, 1));
        assertEquals("Item " + item.getId() + " is unavailable now", thrown.getMessage());

        verify(bookingRepository).findAllPastByBooker(booker);
        verifyNoMoreInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void findItemByUserIdAndItemIdNotOwner() {
        when(userRepository.findById(2)).thenReturn(Optional.of(userForTest));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        ItemResponseDto responseDto = itemService.findItemByUserIdAndItemId(1, 2);

        assertEquals(ItemMapper.INSTANCE.toItemResponseDto(item, null, null), responseDto);

        verify(userRepository).findById(2);
        verify(itemRepository).findById(1);
        verify(bookingService, never()).getLastBookingByItem(item);
        verify(bookingService, never()).getNextBookingByItem(item);
    }

    @Test
    void findItemByUserIdAndItemIdUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findItemByUserIdAndItemId(1, 1));

        verify(userRepository).findById(1);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(bookingService);
    }

    @Test
    void findItemByUserIdAndItemIdItemNotFound() {
        when(userRepository.findById(2)).thenReturn(Optional.of(userForTest));
        when(itemRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findItemByUserIdAndItemId(1, 2));

        verify(userRepository).findById(2);
        verify(itemRepository).findById(1);
        verifyNoInteractions(bookingService);
    }

    @Test
    void deleteWhenUserIsAuthorizedShouldDeleteItem() {
        Integer itemId = 1;
        Integer userId = 2;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemWithoutIdUser2));

        itemService.delete(itemId, userId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void deleteWhenUserIsNotAuthorizedShouldThrowException() {
        Integer itemId = 1;
        Integer userId = 1;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemWithoutIdUser2));

        assertThrows(UnauthorizedException.class, () -> itemService.delete(itemId, userId));
        verify(itemRepository, never()).deleteById(itemId);
    }

    @Test
    void deleteWhenItemNotFoundShouldThrowException() {
        Integer itemId = 1;
        Integer userId = 1;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.delete(itemId, userId));
        verify(itemRepository, never()).deleteById(itemId);
    }
}