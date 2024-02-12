package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(imports = {LocalDateTime.class, UUID.class, ItemMapper.class})
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "requestId", expression = "java(getRequestIdFromItem(item))")
    ItemDto toItemDto(Item item);

    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "description", source = "itemDto.description")
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "itemRequest", source = "itemRequest")
    Item toItem(ItemDto itemDto, User owner, ItemRequest itemRequest);

    @Mapping(target = "lastBooking", expression = "java(getBookingDtoIfExist(lastBooking))")
    @Mapping(target = "nextBooking", expression = "java(getBookingDtoIfExist(nextBooking))")
    @Mapping(target = "requestId", expression = "java(getRequestIdFromItem(item))")
    @Mapping(target = "comments", expression = "java(getCommentsList(item))")
    @Mapping(target = "id", source = "item.id")
    ItemResponseDto toItemResponseDto(Item item, Booking lastBooking, Booking nextBooking);

    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    @Mapping(target = "id", constant = "0L")
    Comment toComment(CommentCreateDto commentCreateDto, User author, Item item);

    @Mapping(target = "itemName", source = "comment.item.name")
    @Mapping(target = "authorName", source = "comment.author.name")
    @Mapping(target = "created", source = "comment.created")
    CommentResponseDto toCommentResponseDto(Comment comment);

    default BookingDto getBookingDtoIfExist(Booking booking) {
        return booking == null
                ? null
                : BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    default Integer getRequestIdFromItem(Item item) {
        return item.getItemRequest() == null ? null : item.getItemRequest().getId();
    }

    default List<CommentResponseDto> getCommentsList(Item item) {
        return item.getComments().stream()
                .map(ItemMapper.INSTANCE::toCommentResponseDto)
                .collect(Collectors.toList());
    }
}
