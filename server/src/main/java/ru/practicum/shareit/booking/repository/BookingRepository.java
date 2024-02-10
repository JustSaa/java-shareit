package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findAllByBookerOrderByStartDesc(User booker, Pageable pageable);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker = :booker AND" +
            " b.start > current_timestamp()" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllFutureByBooker(User booker, Pageable pageable);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker = :booker AND" +
            " b.end < current_timestamp()" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllPastByBooker(User booker, Pageable pageable);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker = :booker AND" +
            " b.end < current_timestamp()" +
            " ORDER BY b.start DESC")
    List<Booking> findAllPastByBooker(User booker);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker = :booker AND" +
            " b.start < current_timestamp() AND b.end > current_timestamp()" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllCurrentByBooker(User booker, Pageable pageable);

    Page<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status, Pageable pageable);

    Page<Booking> findAllByItemOwnerOrderByStartDesc(User owner, Pageable pageable);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner AND" +
            " b.start > current_timestamp()" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllFutureByOwner(User owner, Pageable pageable);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner AND" +
            " b.end < current_timestamp()" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllPastByOwner(User owner, Pageable pageable);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner AND" +
            " b.start < current_timestamp() AND b.end > current_timestamp()" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllCurrentByOwner(User owner, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(User owner, BookingStatus status, Pageable pageable);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item = :item AND" +
            " (b.end < current_timestamp() OR b.start < current_timestamp() AND b.end > current_timestamp())" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllPastOrCurrentByItemDesc(Item item, Pageable pageable);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item = :item AND" +
            " b.start > current_timestamp()" +
            " AND b.status = 'APPROVED'" +
            " ORDER BY b.start ASC")
    Page<Booking> findAllFutureByItemAsc(Item item, Pageable pageable);
}