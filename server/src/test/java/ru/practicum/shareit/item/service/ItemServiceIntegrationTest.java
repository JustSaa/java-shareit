package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:mem:shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
public class ItemServiceIntegrationTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private ItemService itemService;

    @Test
    public void checkGetAllByUserIdTwoItems() {
        List<ItemResponseDto> items = itemService.findAllItems(2, 0, 2);

        assertThat(items, notNullValue());
        assertThat(items.size(), equalTo(2));
        assertThat(items.get(0).getId(), is(in(List.of(1, 3))));
        assertThat(items.get(0).getName(), is(in(List.of("Отвертка-мультитул", "Велик"))));
        assertThat(items.get(1).getId(), is(in(List.of(1, 3))));
        assertThat(items.get(1).getName(), is(in(List.of("Отвертка-мультитул", "Велик"))));
    }

    @Test
    public void checkGetAllByUserIdOneItems() {
        List<ItemResponseDto> items = itemService.findAllItems(2, 0, 1);

        assertThat(items, notNullValue());
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(1));
        assertThat(items.get(0).getName(), equalTo("Отвертка-мультитул"));
    }

    @Test
    public void checkGetAllByTemplate() {
        List<ItemDto> items = itemService.searchItem(2,"веЛо", 0, 2);

        assertThat(items, notNullValue());
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(3));
        assertThat(items.get(0).getName(), equalTo("Велик"));
    }
}