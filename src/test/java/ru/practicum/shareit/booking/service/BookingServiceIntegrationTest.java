package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.in;
import static datas.ObjectMaker.makeItem;
import static datas.ObjectMaker.makeUser;

@Transactional
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:mem:shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
public class BookingServiceIntegrationTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private BookingService bookingService;

    @Test
    public void checkGetBookingRequestsByUserIdAll() {
        List<BookingResponseDto> bookings = bookingService.getBookingRequestsByUserId(1, "ALL", 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(5));
        assertThat(bookings.get(0).getId(), is(in(List.of(2, 4, 5, 6, 7))));
        assertThat(bookings.get(1).getId(), is(in(List.of(2, 4, 5, 6, 7))));
        assertThat(bookings.get(2).getId(), is(in(List.of(2, 4, 5, 6, 7))));
        assertThat(bookings.get(3).getId(), is(in(List.of(2, 4, 5, 6, 7))));
    }

    @Test
    public void checkGetBookingRequestsByUserIdPast() {
        List<BookingResponseDto> bookings = bookingService.getBookingRequestsByUserId(1, "PAST", 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(4, 5))));
        assertThat(bookings.get(1).getId(), is(in(List.of(4, 5))));
    }

    @Test
    public void checkGetBookingRequestsByUserIdCurrent() {
        List<BookingResponseDto> bookings = bookingService.getBookingRequestsByUserId(1, "CURRENT", 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(7));
    }

    @Test
    public void checkGetBookingRequestsByUserIdFuture() {
        List<BookingResponseDto> bookings = bookingService.getBookingRequestsByUserId(1, "FUTURE", 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(2, 6))));
        assertThat(bookings.get(1).getId(), is(in(List.of(2, 6))));
    }

    @Test
    public void checkGetBookingRequestsByUserIdWaiting() {
        List<BookingResponseDto> bookings = bookingService.getBookingRequestsByUserId(3, "WAITING", 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(3));
    }

    @Test
    public void checkGetBookingRequestsByUserIdRejected() {
        List<BookingResponseDto> bookings = bookingService.getBookingRequestsByUserId(1, "REJECTED", 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(4));
    }

    @Test
    public void checkGetBookingsByOwnerIdAll() {
        List<BookingResponseDto> bookings = bookingService.getBookingsByOwnerId(3, "ALL", 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(5));
        assertThat(bookings.get(0).getId(), is(in(List.of(1, 4, 5, 6, 7))));
        assertThat(bookings.get(1).getId(), is(in(List.of(1, 4, 5, 6, 7))));
        assertThat(bookings.get(2).getId(), is(in(List.of(1, 4, 5, 6, 7))));
        assertThat(bookings.get(3).getId(), is(in(List.of(1, 4, 5, 6, 7))));
        assertThat(bookings.get(4).getId(), is(in(List.of(1, 4, 5, 6, 7))));
    }

    @Test
    public void checkGetBookingsByOwnerIdCurrent() {
        List<BookingResponseDto> bookings = bookingService.getBookingsByOwnerId(3, "CURRENT", 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(1, 7))));
        assertThat(bookings.get(1).getId(), is(in(List.of(1, 7))));
    }

    @Test
    public void checkGetBookingsByOwnerIdFuture() {
        List<BookingResponseDto> bookings = bookingService.getBookingsByOwnerId(3, "FUTURE", 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(6));
    }

    @Test
    public void checkGetBookingsByOwnerIdPast() {
        List<BookingResponseDto> bookings = bookingService.getBookingsByOwnerId(3, "PAST", 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(4, 5))));
        assertThat(bookings.get(1).getId(), is(in(List.of(4, 5))));
    }

    @Test
    public void checkGetBookingsByOwnerIdRejected() {
        List<BookingResponseDto> bookings = bookingService.getBookingsByOwnerId(3, "REJECTED", 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(4));
    }

    @Test
    public void checkGetLastBookingByItem() {
        User user = makeUser(3, "Maria", "maria@ya.ru");
        Item item = makeItem(4, "Дрель", "На аккумуляторе",
                true, user, null, null);
        Booking booking = bookingService.getLastBookingByItem(item);

        assertThat(booking, notNullValue());
        assertThat(booking.getId(), equalTo(4));
    }

    @Test
    public void checkGetNextBookingByItem() {
        User user = makeUser(2, "Anna", "anna@ya.ru");
        Item item = makeItem(3, "Велик", "Старый велосипед, требуется замена цепи",
                true, user, null, null);
        Booking booking = bookingService.getNextBookingByItem(item);

        assertThat(booking, notNullValue());
        assertThat(booking.getId(), equalTo(2));
    }
}