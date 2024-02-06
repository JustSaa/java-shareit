package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.EmailNotUniqueException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static datas.ObjectMaker.makeUser;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    private User user;
    private User userWithoutId;

    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImpl(userRepository);
        user = makeUser(1, "Василий", "vasya@ya.ru");
        userWithoutId = makeUser(null, "Василий", "vasya@ya.ru");
    }

    @Test
    public void checkSaveUser() {
        when(userRepository.save(userWithoutId)).thenReturn(user);

        User userFromDb = userService.create(userWithoutId);
        assertNotNull(userFromDb);
        assertEquals(user, userFromDb);

        verify(userRepository).save(userWithoutId);
        verify(userRepository, times(1)).save(userWithoutId);
    }

    @Test
    public void checkUpdateUser_notFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        final var thrown = assertThrows(NotFoundException.class,
                () -> userService.update(user, user.getId()));
        assertEquals("Нет пользователя с id = " + user.getId(), thrown.getMessage());

        verify(userRepository).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void checkUpdateUser_updated() {
        User userAfterUpdate = makeUser(1, "Николай", "nick@ya.ru");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User userAfterTest = userService.update(userAfterUpdate, 1);
        assertEquals(userAfterUpdate, userAfterTest);
    }

    @Test
    public void checkDeleteUser() {
        userService.delete(1);

        verify(userRepository).deleteById(1);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void checkGetUser_notFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        final var thrown = assertThrows(NotFoundException.class, () -> userService.findById(1));
        assertEquals("Нет пользователя с id = " + 1, thrown.getMessage());

        verify(userRepository).findById(1);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void checkGetUser_found() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        User foundUser = userService.findById(user.getId());
        assertEquals(user, foundUser);
    }

    @Test
    public void checkGetAll() {
        User user2 = makeUser(2, "Jane", "jane@ya.ru");
        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<User> foundUsers = userService.findAll();
        assertEquals(2, foundUsers.size());
        assertEquals(user, foundUsers.get(0));
        assertEquals(user2, foundUsers.get(1));

        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testCreateUser_DuplicateEmail() {
        User user = new User();
        user.setEmail("existing@example.com");

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        assertThrows(DuplicateException.class, () -> userService.validationEmail(user));
    }

    @Test
    public void testCreateUser_UniqueEmail() {
        User user = new User();
        user.setEmail("existing@example.com");

        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(EmailNotUniqueException.class, () -> userService.create(user));
    }
}