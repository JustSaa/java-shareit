package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findAllByRequesterOrderByCreatedDesc(User requester);

    @Query("SELECT r FROM ItemRequest AS r WHERE r.requester.id <> ?1 " +
            "ORDER BY r.created DESC")
    Page<ItemRequest> findAllAlien(Integer requesterId, Pageable pageable);
}