package datas;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class ObjectMaker {
    public static User makeUser(Integer id, String name, String email) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    public static UserDto makeUserDto(Integer id, String name, String email) {
        return UserDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    public static Item makeItem(Integer id, String name, String description, Boolean available,
                                User owner, List<Comment> comments, ItemRequest itemRequest) {
        return Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .comments(comments)
                .itemRequest(itemRequest)
                .build();
    }

    public static ItemDto makeItemDto(Integer id, String name, String description,
                                      Boolean available, Integer requestId) {
        return ItemDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .requestId(requestId)
                .build();
    }

    public static ItemRequest makeRequest(Integer id, String description, LocalDateTime created,
                                          User requester, List<Item> items) {
        return ItemRequest.builder()
                .id(id)
                .description(description)
                .created(created)
                .requester(requester)
                .items(items)
                .build();
    }

    public static ItemResponseDto makeItemResponseDto(Integer id, String name, String description,
                                                      Boolean available, Integer requestId,
                                                      BookingResponseDto lastBooking,
                                                      BookingResponseDto nextBooking,
                                                      List<CommentResponseDto> comments) {
        return ItemResponseDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .lastBooking(lastBooking == null ? null : new BookingDto(lastBooking.getId(),
                        lastBooking.getStart(), lastBooking.getEnd(), lastBooking.getBooker().getId()))
                .nextBooking(nextBooking == null ? null : new BookingDto(nextBooking.getId(),
                        nextBooking.getStart(), nextBooking.getEnd(), nextBooking.getBooker().getId()))
                .comments(comments)
                .requestId(requestId)
                .build();
    }

    public static ItemRequestCreateDto makeItemRequestCreateDto(String description) {
        return ItemRequestCreateDto.builder()
                .description(description)
                .build();
    }

    public static Booking makeBooking(Integer id, LocalDateTime start, LocalDateTime end, BookingStatus status,
                                      Item item, User booker) {
        return Booking.builder()
                .id(id)
                .start(start)
                .end(end)
                .status(status)
                .item(item)
                .booker(booker)
                .build();
    }

    public static BookingDto makeBookingDto(Integer id, LocalDateTime start, LocalDateTime end, Integer bookerId) {
        return BookingDto.builder()
                .id(id)
                .start(start)
                .end(end)
                .bookerId(bookerId)
                .build();
    }

    public static BookingCreateDto makeBookingCreateDto(LocalDateTime start, LocalDateTime end,
                                                        Integer itemId, Integer userId) {
        return BookingCreateDto.builder()
                .start(start)
                .end(end)
                .itemId(itemId)
                .userId(userId)
                .build();
    }

    public static BookingResponseDto makeBookingResponseDto(Integer id, LocalDateTime start, LocalDateTime end,
                                                            BookingStatus status, Item item, User booker) {
        return BookingResponseDto.builder()
                .id(id)
                .start(start)
                .end(end)
                .item(new ItemDto(item.getId(), item.getName()))
                .booker(new UserDto(booker.getId()))
                .status(status)
                .build();
    }

    public static CommentResponseDto makeCommentResponseDto(Long id, String text, String itemName,
                                                            String authorName, LocalDateTime created) {
        return CommentResponseDto.builder()
                .id(id)
                .text(text)
                .itemName(itemName)
                .authorName(authorName)
                .created(created)
                .build();
    }

    public static Comment makeComment(Long id, String text, Item item, User author, LocalDateTime created) {
        return Comment.builder()
                .id(id)
                .text(text)
                .item(item)
                .author(author)
                .created(created)
                .build();
    }

    public static CommentCreateDto makeCreateComment(String text) {
        return CommentCreateDto.builder()
                .text(text)
                .build();
    }

    public static ItemRequest makeItemRequest(Integer id, String description, LocalDateTime created,
                                              User requester, List<Item> items) {
        return ItemRequest.builder()
                .id(id)
                .description(description)
                .created(created)
                .requester(requester)
                .items(items)
                .build();
    }

    public static ItemRequestResponseDto makeItemRequestResponseDto(Integer id, String description,
                                                                    LocalDateTime created, User requester,
                                                                    List<ItemResponseDto> items) {
        return ItemRequestResponseDto.builder()
                .id(id)
                .description(description)
                .created(created)
                .requester(new UserDto(requester.getId(), requester.getName(), requester.getEmail()))
                .items(items)
                .build();
    }
}