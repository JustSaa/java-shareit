package ru.practicum.shareit.user.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import datas.LocalDateTimeAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static datas.ObjectMaker.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Captor
    private ArgumentCaptor<User> captor;

    private User user;
    private User userWithoutId;
    private UserDto userDto;
    private UserDto userDtoWithoutId;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @BeforeEach
    void setUp() {
        user = makeUser(2, "Tom", "tom@ya.ru");
        userWithoutId = makeUser(null, "Tom", "tom@ya.ru");
        userDto = makeUserDto(2, "Tom", "tom@ya.ru");
        userDtoWithoutId = makeUserDto(null, "Tom", "tom@ya.ru");
    }

    @Test
    public void checkGetAll() throws Exception {
        List<User> users = List.of(user);
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(users)));

        verify(userService, times(1)).findAll();
    }

    @Test
    public void checkSaveUser() throws Exception {
        when(userService.create(any())).thenReturn(user);


        mockMvc.perform(post("/users")
                        .content(gson.toJson(userDtoWithoutId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(gson.toJson(userDto)));

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    public void checkUpdateUser_validException() throws Exception {
        userDto.setName("");
        mockMvc.perform(patch("/users/{userId}", 2)
                        .content(gson.toJson(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void checkUpdateUser_updated() throws Exception {
        when(userService.update(any(), eq(2))).thenReturn(user);

        mockMvc.perform(patch("/users/{userId}", 2)
                        .content(gson.toJson(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(userDto)));

        verify(userService).update(any(), eq(2));

        verifyNoMoreInteractions(userService);
    }

    @Test
    public void checkDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 2))
                .andExpect(status().isOk());

        verify(userService).delete(2);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void checkGetUser() throws Exception {
        when(userService.findById(user.getId())).thenReturn(user);

        mockMvc.perform(get("/users/{userId}", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(userDto)));

        verify(userService).findById(user.getId());
        verifyNoMoreInteractions(userService);
    }
}