package ru.practicum.shareit.item.repository;

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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
@ExtendWith(SpringExtension.class)
class ItemJpaRepositoryTest {

    @Autowired
    private ItemJpaRepository itemRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private RequestJpaRepository requestRepository;

    Item item1;

    Item item2;

    Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));


    @BeforeAll
    public void setUp() {
        User user1 = new User(1, "email1@mail.ru", "Name1");
        User user2 = new User(2, "email2@mail.ru", "Name2");
        userRepository.save(user1);
        userRepository.save(user2);

        ItemRequest request = new ItemRequest(1, user1, "Description2",
                LocalDateTime.of(2023, 5, 1, 12, 0, 0));
        requestRepository.save(request);

        item1 = new Item(1, "ItemName1", "Description1", true, user1, null);
        item2 = new Item(2, "ItemName2", "Description2", true, user2, request);
        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @Test
    void search() {
        List<Item> actualList = itemRepository.search("Description1", page);

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals(item1, actualList.get(0));
    }

    @Test
    void findAll() {
        List<Item> actualList = itemRepository.findAll(2, page);

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals(item2, actualList.get(0));
    }

    @Test
    void findByRequestId() {
        List<Item> actualList = itemRepository.findByRequestId(1);

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals(item2, actualList.get(0));
    }

}