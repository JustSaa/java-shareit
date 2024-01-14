package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component("memoryUsersStorage")
public class UserStorageImpl implements UserStorage {
    private Integer userId = 0;
    private final List<User> usersList = new ArrayList<>();

    @Override
    public void create(User user) {
        idUpdate();
        user.setId(userId);
        usersList.add(user);
    }

    @Override
    public Optional<User> findById(Integer userId) {
        return usersList.stream()
                .filter(user -> Objects.equals(user.getId(), userId))
                .findAny();
    }

    @Override
    public List<User> findAll() {
        return usersList;
    }

    @Override
    public User update(User user) {
        usersList.add(user);
        return user;
    }

    @Override
    public void delete(Integer userId) {
        usersList.removeIf(user -> Objects.equals(user.getId(), userId));
    }

    private void idUpdate() {
        userId++;
    }
}
