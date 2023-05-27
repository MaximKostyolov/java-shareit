package ru.practicum.shareit.item.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceJpaImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemServiceJpaImpl itemService;

    @SneakyThrows
    @Test
    void getAllUserItems() {
        int userId = 1;
        mockMvc.perform(get("/items")
                .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).getAllUserItems(userId, 0, 20);
    }

    @SneakyThrows
    @Test
    void create() {
        int userId = 1;
        ItemDto itemToCreate = new ItemDto(1, "Item1", "Description1", true, 1,
                new BookingDto(), new BookingDto(), new ArrayList<>());
        when(itemService.createItem(userId, itemToCreate)).thenReturn(itemToCreate);

        String result = mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemToCreate))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemToCreate), result);
        verify(itemService).createItem(userId, itemToCreate);

    }

    @SneakyThrows
    @Test
    void change() {
        int userId = 1;
        int itemId = 1;
        ItemDto itemToUpdate = new ItemDto(1, "Item1", "Description1", true, 1,
                new BookingDto(), new BookingDto(), new ArrayList<>());

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemToUpdate))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService).changeItem(userId, itemId, itemToUpdate);


    }

    @SneakyThrows
    @Test
    void getItem() {
        int userId = 1;
        int itemId = 1;

        mockMvc.perform(get("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).findItemById(userId, itemId);
    }

    @SneakyThrows
    @Test
    void deleteItem() {
        int userId = 1;
        int itemId = 1;

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).removeItem(userId, itemId);
    }

    @SneakyThrows
    @Test
    void getSearchedItems() {
        String searchRequest = "searchRequest";
        mockMvc.perform(get("/items/search?{search}", searchRequest)
                        .param("text", searchRequest))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).getSearchedItems(searchRequest, 0, 20);
    }

    @SneakyThrows
    @Test
    void createComment() {
        int userId = 1;
        int itemId = 1;
        Comment comment = new Comment(1, "text",
                new Item(1, "name", "Description", true, new User(), null),
                new User(1, "Mail@mail.ru", "Name"),
                LocalDateTime.of(2023, 5, 9, 23, 0, 0));
        CommentDto commentDto = new CommentDto(1, "text", "Name",
                LocalDateTime.of(2023, 5, 9, 23, 0, 0));
        when(itemService.createComment(userId, itemId, comment)).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(comment))
                    .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
        verify(itemService).createComment(userId, itemId, comment);

    }

}