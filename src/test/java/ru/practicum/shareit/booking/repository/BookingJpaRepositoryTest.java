package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
@ExtendWith(SpringExtension.class)
class BookingJpaRepositoryTest {

    @Autowired
    private BookingJpaRepository bookingRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private ItemJpaRepository itemRepository;

    Booking booking1;

    Booking booking2;

    Booking booking3;

    Booking booking4;

    Booking booking5;

    Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "end"));



    @BeforeAll
    public void setUp() {
        User owner = new User(1, "email1@mail.ru", "Name1");

        User booker = new User(2, "email2@mail.ru", "Name2");

        Item item = new Item(1, "ItemName", "Description", true, owner, null);

        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        booking1 = new Booking(1,
                LocalDateTime.of(2023, 5, 1, 12, 0, 0),
                LocalDateTime.of(2023, 5, 31, 12, 0, 0),
                item,
                booker,
                Status.APPROVED);

        booking2 = new Booking(2,
                LocalDateTime.of(2023, 4, 1, 12, 0, 0),
                LocalDateTime.of(2023, 4, 10, 12, 0, 0),
                item,
                booker,
                Status.REJECTED);

        booking3 = new Booking(3,
                LocalDateTime.of(2023, 4, 20, 12, 0, 0),
                LocalDateTime.of(2023, 4, 25, 12, 0, 0),
                item,
                booker,
                Status.APPROVED);

        booking4 = new Booking(4,
                LocalDateTime.of(2023, 6, 1, 12, 0, 0),
                LocalDateTime.of(2023, 6, 15, 12, 0, 0),
                item,
                booker,
                Status.APPROVED);

        booking5 = new Booking(5,
                LocalDateTime.of(2023, 6, 20, 12, 0, 0),
                LocalDateTime.of(2023, 6, 25, 12, 0, 0),
                item,
                booker,
                Status.WAITING);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        bookingRepository.save(booking4);
        bookingRepository.save(booking5);
    }

    @Test
    void findByBookerId() {
        List<Booking> actualList = bookingRepository.findByBookerId(2, page);

        assertFalse(actualList.isEmpty());
        assertEquals(5, actualList.size());
    }

    @Test
    void findByBookerIdAndEndIsBefore() {
        List<Booking> actualList = bookingRepository.findByBookerIdAndEndIsBefore(2,
                LocalDateTime.of(2023, 5, 1, 12, 0, 0), page);

        assertFalse(actualList.isEmpty());
        assertEquals(2, actualList.size());
        assertEquals(booking3, actualList.get(0));
        assertEquals(booking2, actualList.get(1));
    }

    @Test
    void findByBookerIdAndStartIsAfter() {
        List<Booking> actualList = bookingRepository.findByBookerIdAndStartIsAfter(2,
                LocalDateTime.of(2023, 6, 1, 10, 0, 0), page);

        assertFalse(actualList.isEmpty());
        assertEquals(2, actualList.size());
        assertEquals(booking5, actualList.get(0));
        assertEquals(booking4, actualList.get(1));
    }

    @Test
    void findByBookerIdAndStatus() {
        List<Booking> actualList = bookingRepository.findByBookerIdAndStatus(2,
                Status.REJECTED, page);

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals(booking2, actualList.get(0));
    }

    @Test
    void findCurrentByBookerId() {
        List<Booking> actualList = bookingRepository.findCurrentByBookerId(2, page);

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals(booking1, actualList.get(0));
    }

    @Test
    void findByOwnerId() {
        List<Booking> actualList = bookingRepository.findByOwnerId(1, page);

        assertFalse(actualList.isEmpty());
        assertEquals(5, actualList.size());
    }

    @Test
    void findCurrentByOwnerId() {
        List<Booking> actualList = bookingRepository.findCurrentByOwnerId(1, page);

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals(booking1, actualList.get(0));
    }

    @Test
    void findPastByOwnerId() {
        List<Booking> actualList = bookingRepository.findPastByOwnerId(1, page);

        assertFalse(actualList.isEmpty());
        assertEquals(2, actualList.size());
        assertEquals(booking3, actualList.get(0));
        assertEquals(booking2, actualList.get(1));
    }

    @Test
    void findFutureByOwnerId() {
        List<Booking> actualList = bookingRepository.findFutureByOwnerId(1, page);

        assertFalse(actualList.isEmpty());
        assertEquals(2, actualList.size());
        assertEquals(booking5, actualList.get(0));
        assertEquals(booking4, actualList.get(1));
    }

    @Test
    void findWaitingByOwnerId() {
        List<Booking> actualList = bookingRepository.findWaitingByOwnerId(1, page);

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals(booking5, actualList.get(0));
    }

    @Test
    void findRejectedByOwnerId() {
        List<Booking> actualList = bookingRepository.findRejectedByOwnerId(1, page);

        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals(booking2, actualList.get(0));
    }

    @Test
    void findByItemId() {
        List<Booking> actualList = bookingRepository.findByItemId(1);

        assertFalse(actualList.isEmpty());
        assertEquals(5, actualList.size());
    }

    @Test
    void findLastBookingForItem() {
        Optional<Booking> actualBooking = bookingRepository.findLastBookingForItem(1);

        assertEquals(booking1, actualBooking.get());
    }

    @Test
    void findNextBookingForItem() {
        Optional<Booking> actualBooking = bookingRepository.findNextBookingForItem(1);

        assertEquals(booking4, actualBooking.get());
    }

    @Test
    void findByBookerIdAndItemId() {
        List<Booking> actualList = bookingRepository.findByBookerIdAndItemId(2,1,
                Sort.by(Sort.Direction.DESC, "end"));

        assertFalse(actualList.isEmpty());
        assertEquals(5, actualList.size());
    }

}