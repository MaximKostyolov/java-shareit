package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
@ExtendWith(SpringExtension.class)
class RequestJpaRepositoryTest {

    @Autowired
    private RequestJpaRepository requestRepository;

    @Autowired
    private UserJpaRepository userRepository;

    private ItemRequest request1;

    private ItemRequest request2;


    @BeforeAll
    public void setUp() {
        User user1 = new User(1, "email1@mail.ru", "Name1");
        User user2 = new User(2, "email2@mail.ru", "Name2");
        userRepository.save(user1);
        userRepository.save(user2);

        request1 = new ItemRequest(1, user1, "Description1",
                LocalDateTime.of(2023, 4, 1, 12, 0, 0));
        request2 = new ItemRequest(2, user2, "Description2",
                LocalDateTime.of(2023, 5, 1, 12, 0, 0));
        requestRepository.save(request1);
        requestRepository.save(request2);
    }

    @Test
    void findByUserId() {
        List<ItemRequest> actualList = requestRepository.findByUserId(2);

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals(request2, actualList.get(0));
    }

    @Test
    void findAllRequests() {
        Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
        List<ItemRequest> actualList = requestRepository.findAllRequests(2, page);

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals(request1, actualList.get(0));
    }

}