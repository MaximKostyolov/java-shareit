package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentJpaRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.validator.Validator;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceJpaImplTest {

    @Mock
    private ItemJpaRepository itemRepository;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private BookingJpaRepository bookingRepository;

    @Mock
    private CommentJpaRepository commentRepository;

    @Mock
    private RequestJpaRepository requestRepository;

    @Mock
    private Validator validator;

    private User expectedUser = User.builder()
            .id(1)
            .name("User1")
            .email("email@mail.ru")
            .build();

    private Item expectedItem = Item.builder()
            .id(1)
            .name("Name")
            .description("Description")
            .available(true)
            .owner(expectedUser)
            .itemRequest(null)
            .build();

    @InjectMocks
    private ItemServiceJpaImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemCapture;

    @Test
    void getAllUserItems_thenReturnedUserItems() {
        int userId = 1;
        List<Item> expectedList = new ArrayList<>();
        Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));

        when(itemRepository.findAll(userId, page)).thenReturn(expectedList);

        List<ItemDto> actualList = itemService.getAllUserItems(userId, 0, 20);
        assertEquals(ItemMapper.itemsToItemDtos(expectedList), actualList);
    }

    @Test
    void createItem_whenValid_thenReturnedItem() {
        int userId = 1;
        int itemId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        when(itemRepository.save(expectedItem)).thenReturn(expectedItem);

        ItemDto actualItem = itemService.createItem(userId, ItemMapper.itemToItemDto(expectedItem));
        assertEquals(expectedItem.getName(), actualItem.getName());
        assertEquals(expectedItem.getDescription(), actualItem.getDescription());
        assertEquals(expectedItem.getAvailable(), actualItem.getAvailable());
        verify(itemRepository).save(expectedItem);
    }

    @Test
    void changeItem_whenValid_thenReturnedItem() {
        int userId = 1;
        int itemId = 1;

        Item newItem = Item.builder()
                .id(1)
                .name("newName")
                .description("newDescription")
                .available(true)
                .owner(expectedUser)
                .itemRequest(null)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(itemRepository.save(newItem)).thenReturn(newItem);

        ItemDto actualItem = itemService.changeItem(userId, itemId, ItemMapper.itemToItemDto(newItem));
        verify(itemRepository).save(itemCapture.capture());
        Item savedItem = itemCapture.getValue();
        assertEquals(newItem.getName(), actualItem.getName());
        assertEquals(newItem.getDescription(), actualItem.getDescription());
        assertEquals(ItemMapper.itemToItemDto(savedItem, new ArrayList<>()), actualItem);
    }

    @Test
    void findItemById_whenFound_thenReturhedItem() {
        int userId = 1;
        int itemId = 1;

        when(validator.validateUser(userId, userRepository)).thenReturn(true);
        when(validator.validateItem(itemId, itemRepository)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        ItemDto actualItem = itemService.findItemById(userId, itemId);

        assertEquals(ItemMapper.itemToItemDto(expectedItem, new ArrayList<>()), actualItem);

    }

    @Test
    void getSearchedItems_whenValid_thenReturnedListOfItems() {
        String searchRequest = "Name";
        List<Item> expectedList = new ArrayList<>();
        expectedList.add(expectedItem);
        List<ItemDto> searchedItems = new ArrayList<>();
        expectedList.forEach(item -> searchedItems.add(ItemMapper.itemToItemDto(item, new ArrayList<>())));
        Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));

        when(itemRepository.search(searchRequest, page)).thenReturn(expectedList);

        List<ItemDto> actualList = itemService.getSearchedItems(searchRequest, 0, 20);

        assertEquals(searchedItems, actualList);
    }

    @Test
    void createComment_whenValid_thenReturnedComment() {
        int itemId = 1;
        User user = User.builder()
                .id(2)
                .name("User2")
                .email("user2@mail.ru")
                .build();
        Booking booking = Booking.builder()
                .id(1)
                .booker(user)
                .item(expectedItem)
                .start(LocalDateTime.now().minus(Period.ofDays(10)))
                .end(LocalDateTime.now().minus(Period.ofDays(5)))
                .status(Status.APPROVED)
                .build();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        Comment comment = Comment.builder()
                .id(1)
                .text("Comment1")
                .build();

        when(validator.validateUser(user.getId(), userRepository)).thenReturn(true);
        when(validator.validateItem(itemId, itemRepository)).thenReturn(true);
        doReturn(bookingList).when(bookingRepository).findByBookerIdAndItemId(user.getId(), itemId,
                Sort.by(Sort.Direction.DESC, "id"));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(commentRepository.save(comment)).thenReturn(comment);

        CommentDto actualComment = itemService.createComment(user.getId(), itemId, comment);

        assertEquals(user.getName(), actualComment.getAuthorName());
        assertEquals(comment.getText(), actualComment.getText());
        assertEquals(expectedItem, comment.getItem());
        verify(commentRepository).save(comment);
    }

}