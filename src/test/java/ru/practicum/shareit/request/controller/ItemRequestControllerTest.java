package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestServiceJpaImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestServiceJpaImpl requestService;

    @SneakyThrows
    @Test
    void create() {
        int userId = 1;
        ItemRequest itemRequest = new ItemRequest(1, new User(), "description", LocalDateTime.now());

        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestService).createRequest(userId, itemRequest);
    }

    @SneakyThrows
    @Test
    void getAllUserRequests() {
        int userId = 1;

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestService).getAllUserRequests(userId);
    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        int userId = 1;

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestService).getAllRequests(userId, 0, 20);
    }


    @SneakyThrows
    @Test
    void getRequest() {
        int userId = 1;
        int requestId = 1;

        mockMvc.perform(get("/requests/{requestId}", requestId)
                            .header("X-Sharer-User-Id", userId))
                    .andExpect(status().isOk());

        verify(requestService).findRequestById(userId, requestId);
    }

}