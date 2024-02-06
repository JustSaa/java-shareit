package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest() == null ? null : item.getItemRequest().getId())
                .build();
    }

    public Item toItem(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description((itemDto.getDescription()))
                .available(itemDto.getAvailable())
                .owner(owner)
                .itemRequest(itemRequest)
                .comments(Collections.emptyList())
                .build();

    }

    public ItemResponseDto toItemResponseDto(Item item,
                                             Booking lastBooking,
                                             Booking nextBooking) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(getBookingDtoIfExist(lastBooking))
                .nextBooking(getBookingDtoIfExist(nextBooking))
                .comments(item.getComments().stream()
                        .map(ItemMapper::toCommentResponseDto)
                        .collect(Collectors.toList()))
                .requestId(item.getItemRequest() == null ? null : item.getItemRequest().getId())
                .build();
    }

    public Comment toComment(CommentCreateDto commentCreateDto, User author, Item item) {
        return Comment.builder()
                .text(commentCreateDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    public CommentResponseDto toCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemName(comment.getItem().getName())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    private BookingDto getBookingDtoIfExist(Booking booking) {
        return booking == null
                ? null
                : BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
