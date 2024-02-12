package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.in;
import static datas.ObjectMaker.makeItem;
import static datas.ObjectMaker.makeUser;

@DataJpaTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;

    private final PageRequest pr = PageRequest.of(0, 5);

    private User booker;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        booker = makeUser(1, "Petr", "petr@ya.ru");
        owner = makeUser(3, "Maria", "maria@ya.ru");
        item = makeItem(6, "Мультиварка", "В хорошем состоянии",
                true, null, null, null);
    }

    @Test
    public void checkFindAllFutureByBooker() {
        List<Booking> bookings = bookingRepository.findAllFutureByBooker(booker, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(2, 6))));
        assertThat(bookings.get(1).getId(), is(in(List.of(2, 6))));
    }

    @Test
    public void checkFindAllPastByBooker() {
        List<Booking> bookings = bookingRepository.findAllPastByBooker(booker, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(4, 5))));
        assertThat(bookings.get(1).getId(), is(in(List.of(4, 5))));
    }

    @Test
    public void checkFindAllCurrentByBooker() {
        List<Booking> bookings = bookingRepository.findAllPastByBooker(booker, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(4, 5))));
        assertThat(bookings.get(1).getId(), is(in(List.of(4, 5))));
    }

    @Test
    public void checkFindAllFutureByOwner() {
        List<Booking> bookings = bookingRepository.findAllFutureByOwner(owner, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(6));
    }

    @Test
    public void checkFindAllPastByOwner() {
        List<Booking> bookings = bookingRepository.findAllPastByOwner(owner, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(4, 5))));
        assertThat(bookings.get(1).getId(), is(in(List.of(4, 5))));
    }

    @Test
    public void checkFindAllCurrentByOwner() {
        List<Booking> bookings = bookingRepository.findAllCurrentByOwner(owner, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(1, 7))));
        assertThat(bookings.get(1).getId(), is(in(List.of(1, 7))));
    }

    @Test
    public void checkFindAllPastOrCurrentByItemDesc() {
        List<Booking> bookings = bookingRepository.findAllPastOrCurrentByItemDesc(item, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(7));
    }

    @Test
    public void checkFindAllFutureByItemAsc() {

        List<Booking> bookings = bookingRepository.findAllFutureByItemAsc(item, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(6));
    }
}