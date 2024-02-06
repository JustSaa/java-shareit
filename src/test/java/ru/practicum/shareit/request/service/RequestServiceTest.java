package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static datas.ObjectMaker.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    private RequestService requestService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;

    private User requester;
    private ItemRequest requestWithoutId;
    private ItemRequest request;
    private ItemRequestCreateDto requestDesc;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl(requestRepository, userRepository);
        requester = makeUser(1, "Olya", "olya@ya.ru");
        requestWithoutId = makeItemRequest(null, "description",
                LocalDateTime.of(2022, 10, 10, 10, 10, 10), requester, null);
        request = makeItemRequest(1, "description",
                LocalDateTime.of(2022, 10, 10, 10, 10, 10), requester, null);
        requestDesc = makeItemRequestCreateDto("description");
    }

    @Test
    public void testSaveRequest_Success() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequest savedRequest = requestService.saveRequest(requester.getId(),requestDesc);

        assertEquals(request.getDescription(), savedRequest.getDescription());
    }

    @Test
    public void checkGetAllByRequester() {
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(requestRepository.findAllByRequesterOrderByCreatedDesc(any())).thenReturn(List.of(request));

        List<ItemRequest> requests = requestService.getAllByRequester(1);
        assertEquals(1, requests.size());
        assertEquals(request, requests.get(0));

        verify(requestRepository).findAllByRequesterOrderByCreatedDesc(requester);
        verifyNoMoreInteractions(requestRepository);
    }

    @Test
    public void checkGetRequestById() {
        when(requestRepository.findById(requester.getId())).thenReturn(Optional.of(request));

        ItemRequest requestFromDb = requestService.getRequestById(requester.getId());
        assertEquals(request, requestFromDb);

        verify(requestRepository).findById(requester.getId());
        verifyNoMoreInteractions(requestRepository);
    }

    @Test
    public void checkGetRequestById_requestNotFoundException() {
        when(requestRepository.findById(any())).thenReturn(Optional.empty());

        final var thrown = assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(1));
        assertEquals("Request not found 1", thrown.getMessage());

        verify(requestRepository).findById(any());
        verifyNoMoreInteractions(requestRepository);
    }
}