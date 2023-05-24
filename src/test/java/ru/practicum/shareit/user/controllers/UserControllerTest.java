package ru.practicum.shareit.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceJpaImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceJpaImpl userService;


    @SneakyThrows
    @Test
    void getAllUsers() {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getAllUsers();
    }

    @SneakyThrows
    @Test
    void create() {
        User userToCreate = new User(1, "email@yandex.ru", "Name");
        when(userService.createUser(userToCreate)).thenReturn(UserMapper.userToUserDto(userToCreate));

        String result = mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userToCreate)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(UserMapper.userToUserDto(userToCreate)), result);
        verify(userService).createUser(userToCreate);
    }

    @SneakyThrows
    @Test
    void change_whenUserIsValid_thenReturnedStatusOk() {
        int userId = 1;
        UserDto userToUpdate = new UserDto(1, "email@mail.ru", "ApdatedName");

        mockMvc.perform(patch("/users/{userId}", userId)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk());

        verify(userService).changeUser(userId, userToUpdate);
    }

    @SneakyThrows
    @Test
    void change_whenUserIsNotValid_thenReturnedBadRequest() {
        int userId = 1;
        UserDto userToUpdate = new UserDto(1, "fail_email", "ApdatedName");

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).changeUser(userId, userToUpdate);
    }

    @SneakyThrows
    @Test
    void getUserById() {
        int userId = 1;

        mockMvc.perform(get("/users/{userIid}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).findUserById(userId);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        int userId = 1;

        mockMvc.perform(delete("/users/{userIid}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).removeUser(userId);
    }

}