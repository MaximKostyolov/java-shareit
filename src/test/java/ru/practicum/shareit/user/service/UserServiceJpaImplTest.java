package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceJpaImplTest {

    @Mock
    private UserJpaRepository userRepository;

    @InjectMocks
    private UserServiceJpaImpl userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void getAllUsers_thenReturnedUsers() {
        List<User> expectedList = new ArrayList<>();

        when(userRepository.findAll()).thenReturn(expectedList);

        List<User> actualList = userService.getAllUsers();

        assertEquals(expectedList, actualList);
    }

    @Test
    void createUser_whenUserValid_thenSavedUser() {
        User userToSave = User.builder().name("Name").email("email@mail.ru").build();

        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserDto actualUser = userService.createUser(userToSave);

        assertEquals(UserMapper.userToUserDto(userToSave), actualUser);
        verify(userRepository).save(userToSave);
    }

    @Test
    void createUser_whenUserNotValid_thenResponseStatusExceptionThrown() {
        User userToSave = User.builder().id(1).name("Name").email("email@mail.ru").build();

        when(userRepository.save(userToSave)).thenThrow(RuntimeException.class);

        assertThrows(ResponseStatusException.class,
                () -> userService.createUser(userToSave));
    }

    @Test
    void createUser_whenUserNotValid_thenValidationExceptionThrown() {
        User userToSave = new User();

        assertThrows(ValidationException.class,
                () -> userService.createUser(userToSave));
    }


    @Test
    void changeUser_whenUpdateNameAndEmail_thenReturnedUpdatedUser() {
        int userId = 1;
        User oldUser = User.builder().id(1).name("Name").email("email@mail.ru").build();

        User newUser = User.builder().id(1).name("newName").email("newEmail@.yandex.ru").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(newUser)).thenReturn(newUser);

        UserDto actualUser = userService.changeUser(userId, UserMapper.userToUserDto(newUser));

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals(newUser.getName(), actualUser.getName());
        assertEquals(newUser.getEmail(), actualUser.getEmail());
        assertEquals(UserMapper.userToUserDto(savedUser), actualUser);
    }

    @Test
    void changeUser_whenUpdateName_thenReturnedUpdatedUser() {
        int userId = 1;
        User oldUser = User.builder().id(1).name("Name").email("email@mail.ru").build();

        User newUser = User.builder().id(1).name("newName").build();

        User expectedUser = User.builder().id(1).name("newName").email("email@mail.ru").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        UserDto actualUser = userService.changeUser(userId, UserMapper.userToUserDto(newUser));

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(UserMapper.userToUserDto(savedUser), actualUser);
    }

    @Test
    void changeUser_whenUpdateEmail_thenReturnedUpdatedUser() {
        int userId = 1;
        User oldUser = User.builder().id(1).name("Name").email("email@mail.ru").build();

        User newUser = User.builder().id(1).email("newEmail@.yandex.ru").build();

        User expectedUser = User.builder().id(1).name("Name").email("newEmail@.yandex.ru").build();


        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        UserDto actualUser = userService.changeUser(userId, UserMapper.userToUserDto(newUser));

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(UserMapper.userToUserDto(savedUser), actualUser);
    }

    @Test
    void changeUser_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        int userId = 1;
        UserDto newUser = UserDto.builder().name("newName").email("newEmail@.yandex.ru").build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.changeUser(userId, newUser));
        verify(userRepository, never()).save(userArgumentCaptor.capture());
    }

    @Test
    void findUserById_whenFound_thenReturnedUser() {
        int userId = 0;
        User expectedUser = User.builder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserDto actualUser = userService.findUserById(userId);

        assertEquals(UserMapper.userToUserDto(expectedUser), actualUser);
    }

    @Test
    void findUserById_whenNotFound_thenUserNotFoundExceptionThrown() {
        int userId = 0;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.findUserById(userId));
    }

    @Test
    void removeUser_whenUserNotFound_thenIllegalArgumentExceptionThrown() {
        int userId = 1;

        doThrow(IllegalArgumentException.class).when(userRepository).deleteById(userId);

        assertThrows(IllegalArgumentException.class,
                () -> userService.removeUser(userId));
    }

}