package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> getAllByOwnerIdOrderById(Integer itemId, Pageable pageable);

    @Query("SELECT i FROM Item AS i" +
            " WHERE i.available = true AND" +
            " LOWER(CONCAT(i.description, i.name)) LIKE LOWER(CONCAT('%', :template, '%'))")
    Page<Item> getAllByTemplate(String template, Pageable pageable);
}
