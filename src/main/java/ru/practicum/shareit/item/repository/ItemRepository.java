package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> getAllByOwnerIdOrderById(Integer itemId);

    @Query("SELECT i FROM Item AS i" +
            " WHERE i.available = true AND" +
            " LOWER(CONCAT(i.description, i.name)) LIKE LOWER(CONCAT('%', :template, '%'))")
    List<Item> getAllByTemplate(@Param("template") String template);
}
