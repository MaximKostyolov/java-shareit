package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.validator.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceJpaImplTest {

    @Mock
    private RequestJpaRepository requestRepository;

    @Mock
    private ItemJpaRepository itemRepository;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private Validator validator;

    private User expectedUser = User.builder()
            .id(1)
            .name("User1")
            .email("email@mail.ru")
            .build();

    private ItemRequest request = ItemRequest.builder()
            .id(1)
            .description("Description")
            .user(expectedUser)
            .created(LocalDateTime.of(2023, 4, 17, 12, 0))
            .build();
    @InjectMocks
    private RequestServiceJpaImpl requestService;

    @Captor
    private ArgumentCaptor<ItemRequest> requestCapture;

   @Test
    void createRequest_whenValid_thenSavedRequest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1)
                .description("Description")
                .build();
        ItemRequest expectedRequest = ItemRequest.builder()
                .id(1)
                .description("Description")
                .user(expectedUser)
                .created(LocalDateTime.parse(LocalDateTime.now().plusNanos(250000000).format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .build();

        when(validator.validateUser(expectedUser.getId(), userRepository)).thenReturn(true);
        when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));
        when(requestRepository.save(expectedRequest)).thenReturn(expectedRequest);

        ItemRequestDto actualRequest = requestService.createRequest(expectedUser.getId(), itemRequest);

        assertEquals(expectedRequest.getDescription(), actualRequest.getDescription());
        verify(requestRepository).save(requestCapture.capture());
        ItemRequest savedRequest = requestCapture.getValue();
        assertEquals(RequestMapper.itemRequestToItemRequestDto(savedRequest), actualRequest);
    }

    @Test
    void getAllUserRequests_whenUserFound_thenReturnedUserRequests() {
        List<ItemRequest> expectedList = new ArrayList<>();
        expectedList.add(request);

        when(validator.validateUser(expectedUser.getId(), userRepository)).thenReturn(true);
        doReturn(expectedList).when(requestRepository).findByUserId(expectedUser.getId());

        List<ItemRequestDto> actualRequests = requestService.getAllUserRequests(expectedUser.getId());

        assertEquals(1, actualRequests.size());
        assertEquals(RequestMapper.itemRequestToItemRequestDto(request), actualRequests.get(0));
    }

    @Test
    void getAllRequests_thenReturnedUserRequests() {
       List<ItemRequestDto> expectedList = new ArrayList<>();
       Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));

        when(requestRepository.findAllRequests(expectedUser.getId(), page)).thenReturn(new ArrayList<>());

        List<ItemRequestDto> actualList = requestService.getAllRequests(expectedUser.getId(), 0, 20);

        assertEquals(expectedList, actualList);
    }

    @Test
    void findRequestById_whenFound_thenReturnedRequest() {

       when(validator.validateUser(expectedUser.getId(), userRepository)).thenReturn(true);
       when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

       ItemRequestDto actualRequest = requestService.findRequestById(expectedUser.getId(), request.getId());

       assertEquals(RequestMapper.itemRequestToItemRequestDto(request, new ArrayList<>()), actualRequest);

   }
}