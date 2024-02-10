package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.in;
import static datas.ObjectMaker.makeUser;

@Transactional
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:mem:shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
public class RequestServiceIntegrationTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private RequestService requestService;

    @Test
    public void checkGetAllAlien() {
        User user = makeUser(2, "Anna", "anna@ya.ru");
        List<ItemRequest> requests = requestService.getAllAlien(user.getId(), 0, 5);

        assertThat(requests, notNullValue());
        assertThat(requests.size(), equalTo(2));
        assertThat(requests.get(0).getDescription(), is(in(List.of("Нужна крестовая отвертка", "Велосипед на выходные"))));
        assertThat(requests.get(1).getDescription(), is(in(List.of("Нужна крестовая отвертка", "Велосипед на выходные"))));
    }
}