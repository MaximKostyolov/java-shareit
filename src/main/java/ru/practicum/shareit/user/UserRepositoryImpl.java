package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
public class UserRepositoryImpl implements UserRepository {

    private Integer userId = 0;

    private final Map<Integer, User> USERS = new HashMap<>();

    private final Set<String> EMAILS = new HashSet<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(USERS.values());
    }

    @Override
    public UserDto addUser(User user) {
        if (!EMAILS.contains(user.getEmail())) {
            setUserId(getUserId() + 1);
            user.setId(getUserId());
            USERS.put(user.getId(), user);
            EMAILS.add(user.getEmail());
            log.info("Пользователь создан");
        } else {
            log.info("Пользователь с таким e-mail уже cуществует");
            throw new DuplicateEmailException();
        }
        return UserMapper.userToUserDto(user);
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        User updatedUser;
        if (USERS.containsKey(userId)) {
            User user = USERS.get(userId);
            if (userDto.getName() == null) {
                updatedUser = updateEmail(user, userDto);
            } else if (userDto.getEmail() == null) {
                updatedUser = updateName(user, userDto);
            } else {
                updatedUser = updateNameAndEmail(user, userDto);
            }
            if (updatedUser != null) {
                USERS.remove(userId);
                USERS.put(userId, updatedUser);
                log.info("Пользовател обновлен");
            }
        } else {
            log.info("Пользователь с id = " + userId + " не найден");
            throw new UserNotFoundException();
        }
        return UserMapper.userToUserDto(updatedUser);
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(USERS.get(id));
    }

    @Override
    public void deleteUser(int id) {
        if (USERS.containsKey(id)) {
            EMAILS.remove(USERS.get(id).getEmail());
            USERS.remove(id);
            log.info("Пользователь с id = " + id + " удален");
        } else {
            log.info("Пользователь с id = " + id + " не найден");
            throw new UserNotFoundException();
        }
    }

    private User updateEmail(User user, UserDto userDto) {
        if ((!EMAILS.contains(userDto.getEmail())) || (user.getEmail().equals(userDto.getEmail()))) {
            EMAILS.remove(user.getEmail());
            EMAILS.add(userDto.getEmail());
            return User.builder().
                    id(user.getId()).
                    email(userDto.getEmail()).
                    name(user.getName()).
                    build();
        } else {
            log.info("Пользователь с таким e-mail уже cуществует");
            throw new DuplicateEmailException();
        }
    }

    private User updateName(User user, UserDto userDto) {
        return User.builder().
                id(user.getId()).
                email(user.getEmail()).
                name(userDto.getName()).
                build();
    }

    private User updateNameAndEmail(User user, UserDto userDto) {
        if ((!EMAILS.contains(userDto.getEmail())) || (user.getEmail().equals(userDto.getEmail()))) {
            EMAILS.remove(user.getEmail());
            EMAILS.add(userDto.getEmail());
            return User.builder().
                    id(user.getId()).
                    email(userDto.getEmail()).
                    name(userDto.getName()).
                    build();
        } else {
            log.info("Пользователь с таким e-mail уже cуществует");
            throw new DuplicateEmailException();
        }
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
