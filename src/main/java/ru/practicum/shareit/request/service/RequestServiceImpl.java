package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.custom.CustomPageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemRequest saveRequest(Integer userId, ItemRequestCreateDto itemRequest) {
        User register = getUserFromDB(userId);
        ItemRequest itemRequestToDB = RequestMapper.INSTANCE.toRequest(itemRequest, register);
        requestRepository.save(itemRequestToDB);
        log.debug("Saved request to DB: {}", itemRequestToDB);
        return itemRequestToDB;
    }

    @Override
    public List<ItemRequest> getAllByRequester(Integer userId) {
        User requester = getUserFromDB(userId);
        List<ItemRequest> requests = requestRepository.findAllByRequesterOrderByCreatedDesc(requester);
        log.debug("Requests by userId={}: {}", requester.getId(), requests);
        return requests;
    }

    @Override
    public List<ItemRequest> getAllAlien(Integer userId, int from, int size) {
        User requester = getUserFromDB(userId);
        Pageable pageRequest = new CustomPageRequest(from, size);
        List<ItemRequest> requests = requestRepository.findAllAlien(requester.getId(), pageRequest).getContent();
        log.debug("Requests for userId={}: {}", requester.getId(), requests);
        return requests;
    }

    @Override
    public ItemRequest getRequestById(Integer requestId) {
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(() -> {
            throw new NotFoundException("Request not found " + requestId);
        });
        log.debug("Returned request: {}", request);
        return request;
    }

    private User getUserFromDB(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь с не найден userId: " + userId);
        });
    }
}