package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerOrderByStartDesc(User booker);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker = :booker AND" +
            " b.start > current_timestamp()" +
            " ORDER BY b.start DESC")
    List<Booking> findAllFutureByBooker(User booker);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker = :booker AND" +
            " b.end < current_timestamp()" +
            " ORDER BY b.start DESC")
    List<Booking> findAllPastByBooker(User booker);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker = :booker AND" +
            " b.start < current_timestamp() AND b.end > current_timestamp()" +
            " ORDER BY b.start DESC")
    List<Booking> findAllCurrentByBooker(User booker);

    List<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status);

    List<Booking> findAllByItemOwnerOrderByStartDesc(User owner);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner AND" +
            " b.start > current_timestamp()" +
            " ORDER BY b.start DESC")
    List<Booking> findAllFutureByOwner(User owner);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner AND" +
            " b.end < current_timestamp()" +
            " ORDER BY b.start DESC")
    List<Booking> findAllPastByOwner(User owner);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner AND" +
            " b.start < current_timestamp() AND b.end > current_timestamp()" +
            " ORDER BY b.start DESC")
    List<Booking> findAllCurrentByOwner(User owner);

    List<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(User owner, BookingStatus status);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item = :item AND" +
            " (b.end < current_timestamp() OR b.start < current_timestamp() AND b.end > current_timestamp())" +
            " ORDER BY b.start DESC")
    List<Booking> findAllPastOrCurrentByItemDesc(Item item);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item = :item AND" +
            " b.start > current_timestamp()" +
            " AND b.status = 'APPROVED'" +
            " ORDER BY b.start ASC")
    List<Booking> findAllFutureByItemAsc(Item item);
}