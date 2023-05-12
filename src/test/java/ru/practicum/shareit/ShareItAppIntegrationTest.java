package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceJpaImpl;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceJpaImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestServiceJpaImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceJpaImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ShareItAppIntegrationTest {

    @Autowired
    private UserServiceJpaImpl userService;

    @Autowired
    private ItemServiceJpaImpl itemService;

    @Autowired
    private BookingServiceJpaImpl bookingService;

    @Autowired
    private RequestServiceJpaImpl requestService;

    private User user1 = new User(1, "email1@mail.ru", "Name1");

    private User user2 = new User(2, "email2@mail.ru", "Name2");

    private User user3 = new User(3, "email3@mail.ru", "Name3");

    private Item item1 = new Item(1, "itemName1", "itemDescription1", true, user1,
            null);

    private Item item2 = new Item(2, "itemName2", "itemDescription2", true, user1,
            null);

    private Item item3 = new Item(3, "itemName3", "itemDescription3", true, user2,
            null);

    private ItemRequest itemRequestToCreate = new ItemRequest(1, user2, "requestDesciption1",
            LocalDateTime.now());

    @BeforeAll
    public void setUp() {
        userService.createUser(user1);
        userService.createUser(user2);
        userService.createUser(user3);
        itemService.createItem(1, ItemMapper.itemToItemDto(item1));
        itemService.createItem(1, ItemMapper.itemToItemDto(item2));
        itemService.createItem(2, ItemMapper.itemToItemDto(item3));
    }

    @Test
    public void createItemRequest() {
        requestService.createRequest(2, itemRequestToCreate);

        ItemRequestDto actualRequest = requestService.findRequestById(3, 1);
        List<ItemRequestDto> usersRequests = requestService.getAllUserRequests(2);

        assertEquals(RequestMapper.itemRequestToItemRequestDto(itemRequestToCreate), actualRequest);
        assertEquals(1, usersRequests.size());
        assertEquals(RequestMapper.itemRequestToItemRequestDto(itemRequestToCreate), usersRequests.get(0));
    }

    @Test
    public void checkUsersAndItems() {
        List<User> users = userService.getAllUsers();
        List<ItemDto> user1Items = itemService.getAllUserItems(1, 0, 20);
        List<ItemDto> user2Items = itemService.getAllUserItems(2, 0, 20);
        List<ItemDto> user3Items = itemService.getAllUserItems(3, 0, 20);

        assertEquals(3, users.size());
        assertEquals(user1, users.get(0));
        assertEquals(user2, users.get(1));
        assertEquals(user3, users.get(2));
        assertEquals(2, user1Items.size());
        assertEquals(1, user2Items.size());
        assertEquals(0, user3Items.size());

    }

    @Test
    public void createRequestAndItemForRequest() {
        requestService.createRequest(2, itemRequestToCreate);
        ItemDto itemDto  = new ItemDto(4, "itemName4", "itemDescription4", true, 1,
                null, null, new ArrayList<>());
        itemService.createItem(3, itemDto);

        ItemDto actualItem = itemService.findItemById(3, 4);
        List<ItemDto> itemsForRequest = itemService.getAllUserItems(3, 0, 20);

        assertEquals(itemDto, actualItem);
        assertEquals(1, itemsForRequest.size());
        assertEquals(itemDto, itemsForRequest.get(0));

        itemService.removeItem(3, 4);
    }

    @Test
    public void createAndApproveBooking() {
        BookingDto bookingDto = new BookingDto(1,
                LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd'T'HH:mm:ss"))).plusDays(5),
                LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd'T'HH:mm:ss"))).plusDays(15),
                item3.getId(),
                item3.getName(),
                user1.getId(),
                Status.WAITING);
        Booking expectedBooking = bookingService.createBooking(1, bookingDto);

        Booking actualBooking = bookingService.findBookingById(2, 1);
        List<Booking> userBookings = bookingService.findBookingByUserId(1, "ALL", 0, 20);

        assertEquals(expectedBooking, actualBooking);
        assertEquals(1, userBookings.size());
        assertEquals(expectedBooking, userBookings.get(0));

        Booking expectedBooking2 = bookingService.approvedBooking(2, 1, false);

        List<Booking> approvedBookings = bookingService.findBookingByOwnerId(2, "REJECTED", 0, 20);

        assertEquals(1, approvedBookings.size());
        assertEquals(1, approvedBookings.get(0).getId());
        assertEquals(expectedBooking2, approvedBookings.get(0));
    }

    @Test
    public void createComment() {
        Comment comment = new Comment(1, "comment1", item1, user3, LocalDateTime.now());

        assertThrows(ValidationException.class,
                () -> itemService.createComment(3, 1, comment));
    }

}