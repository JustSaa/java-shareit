package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.in;

@DataJpaTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    private final PageRequest pr = PageRequest.of(0, 5);

    @Test
    public void checkGetAllByOwnerIdOrderById() {
        Page<Item> items = itemRepository.getAllByTemplate("ФоТо", pr);

        assertThat(items, notNullValue());
        assertThat(items.getContent().size(), equalTo(2));
        assertThat(items.getContent().get(0).getId(), is(in(List.of(7, 8))));
        assertThat(items.getContent().get(0).getName(), is(in(List.of("Фотоаппарат", "Фото- и видеокамера"))));
        assertThat(items.getContent().get(1).getId(), is(in(List.of(7, 8))));
        assertThat(items.getContent().get(1).getName(), is(in(List.of("Фотоаппарат", "Фото- и видеокамера"))));
    }
}