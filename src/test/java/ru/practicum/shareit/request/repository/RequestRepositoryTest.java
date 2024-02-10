package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.in;

@DataJpaTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")

class RequestRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private RequestRepository requestRepository;

    private final PageRequest pr = PageRequest.of(0, 5);

    @Test
    public void checkFindAllAlien() {
        List<ItemRequest> requests = requestRepository.findAllAlien(2, pr).getContent();

        assertThat(requests, notNullValue());
        assertThat(requests.size(), equalTo(2));
        assertThat(requests.get(0).getDescription(), is(in(List.of("Нужна крестовая отвертка", "Велосипед на выходные"))));
        assertThat(requests.get(1).getDescription(), is(in(List.of("Нужна крестовая отвертка", "Велосипед на выходные"))));
    }
}