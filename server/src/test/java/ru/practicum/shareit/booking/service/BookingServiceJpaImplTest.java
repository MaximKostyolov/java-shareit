package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.validator.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceJpaImplTest {

    @Mock
    private BookingJpaRepository bookingRepository;

    @Mock
    private ItemJpaRepository itemRepository;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private Validator validator;

    private User owner = User.builder()
            .id(1)
            .name("User1")
            .email("email@mail.ru")
            .build();

    private User booker = User.builder()
            .id(2)
            .name("User2")
            .email("email2@mail.ru")
            .build();

    private Item expectedItem = Item.builder()
            .id(1)
            .name("Name")
            .description("Description")
            .available(true)
            .owner(owner)
            .itemRequest(null)
            .build();

    private Booking expectedBooking = Booking.builder()
            .id(1)
            .item(expectedItem)
            .booker(booker)
            .start(LocalDateTime.of(2023, 6, 17, 12, 0))
            .end(LocalDateTime.of(2023, 6, 27, 12, 00))
            .status(Status.WAITING)
            .build();

    @InjectMocks
    private BookingServiceJpaImpl bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingCapture;

    @Test
    void createBooking_whenBookingValid_thenReturnedBooking() {
        int userId = 2;
        int itemId = 1;
        BookingDto bookingDto = BookingDto.builder()
                .id(1)
                .itemId(itemId)
                .start(LocalDateTime.of(2023, 6, 17, 12, 0))
                .end(LocalDateTime.of(2023, 6, 27, 12, 0))
                .build();

        when(validator.validateUser(userId, userRepository)).thenReturn(true);
        when(validator.validateItem(itemId, itemRepository)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        Booking actualBooking = bookingService.createBooking(userId, bookingDto);

        assertEquals(expectedBooking, actualBooking);
        verify(bookingRepository).save(bookingCapture.capture());
        Booking savedBooking = bookingCapture.getValue();
        assertEquals(expectedBooking, savedBooking);
    }

    @Test
    void createBooking_whenDateNotValid_thenValidationExceptionTrown() {
        int userId = 2;
        int itemId = 1;
        BookingDto bookingDto = BookingDto.builder()
                .id(1)
                .itemId(itemId)
                .start(LocalDateTime.of(2023, 6, 17, 12, 0))
                .end(null)
                .build();

        when(validator.validateUser(userId, userRepository)).thenReturn(true);
        when(validator.validateItem(itemId, itemRepository)).thenReturn(true);

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(userId, bookingDto));
    }

    @Test
    void createBooking_whenBookingNotValid_thenBookingNotFoundExceptionTrown() {
        int userId = owner.getId();
        int itemId = 1;
        BookingDto bookingDto = BookingDto.builder()
                .id(1)
                .itemId(itemId)
                .start(LocalDateTime.of(2023, 6, 17, 12, 0))
                .end(LocalDateTime.of(2023, 6, 27, 12, 0))
                .build();

        when(validator.validateUser(userId, userRepository)).thenReturn(true);
        when(validator.validateItem(itemId, itemRepository)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.createBooking(userId, bookingDto));
    }

    @Test
    void createBooking_whenItemNotAvailable_thenItemNotAvailableExceptionTrown() {
        int userId = 2;
        int itemId = 1;
        expectedItem.setAvailable(Boolean.FALSE);
        BookingDto bookingDto = BookingDto.builder()
                .id(1)
                .itemId(itemId)
                .start(LocalDateTime.of(2023, 6, 17, 12, 0))
                .end(LocalDateTime.of(2023, 6, 27, 12, 0))
                .build();

        when(validator.validateUser(userId, userRepository)).thenReturn(true);
        when(validator.validateItem(itemId, itemRepository)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        assertThrows(ItemNotAvailableException.class,
                () -> bookingService.createBooking(userId, bookingDto));

        expectedItem.setAvailable(Boolean.TRUE);
    }

    @Test
    void createBooking_whenUserOrItemNotValid_thenUserNotFoundExceptionTrown() {
        int userId = 2;
        int itemId = 1;
        BookingDto bookingDto = BookingDto.builder()
                .id(1)
                .itemId(itemId)
                .start(LocalDateTime.of(2023, 6, 17, 12, 0))
                .end(LocalDateTime.of(2023, 6, 27, 12, 0))
                .build();

        when(validator.validateUser(userId, userRepository)).thenReturn(true);
        when(validator.validateItem(itemId, itemRepository)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> bookingService.createBooking(userId, bookingDto));
    }

    @Test
    void approvedBooking_whenValid_thenReturnedBooking() {
        int bookingId = 1;

        when(validator.validateBookingAndItem(bookingId, bookingRepository, itemRepository)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));
        when(itemRepository.findById(expectedBooking.getItem().getId())).thenReturn(Optional.of(expectedItem));
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        Booking actualBooking = bookingService.approvedBooking(owner.getId(), bookingId, true);

        assertEquals(Status.APPROVED, actualBooking.getStatus());
        verify(bookingRepository).save(bookingCapture.capture());
        Booking savedBooking = bookingCapture.getValue();
        assertEquals(Status.APPROVED, savedBooking.getStatus());
    }

    @Test
    void approvedBooking_whenAlreadyAproved_thenBookingApproveExceptionTrown() {
        int userId = 1;
        int bookingId = 1;
        expectedBooking.setStatus(Status.APPROVED);

        when(validator.validateBookingAndItem(bookingId, bookingRepository, itemRepository)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));
        when(itemRepository.findById(expectedBooking.getItem().getId())).thenReturn(Optional.of(expectedItem));

        assertThrows(BookingApproveException.class,
                () -> bookingService.approvedBooking(userId, bookingId, true));
    }

    @Test
    void approvedBooking_whenAccesError_thenAccessErrorExceptionTrown() {
        int userId = 2;
        int bookingId = 1;
        expectedBooking.setStatus(Status.APPROVED);

        when(validator.validateBookingAndItem(bookingId, bookingRepository, itemRepository)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));
        when(itemRepository.findById(expectedBooking.getItem().getId())).thenReturn(Optional.of(expectedItem));

        assertThrows(AccessErrorException.class,
                () -> bookingService.approvedBooking(userId, bookingId, true));

        expectedBooking.setStatus(Status.WAITING);
    }

    @Test
    void approvedBooking_whenBookingOrItemNotFound_thenBookingNotFoundExceptionTrown() {
        int userId = 2;
        int bookingId = 1;

        when(validator.validateBookingAndItem(bookingId, bookingRepository, itemRepository)).thenReturn(false);

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.approvedBooking(userId, bookingId, true));
    }

    @Test
    void findBookingById_whenBookerOrOwnerRequest_thenReturnedBooking() {
        int bookingId = 1;

        when(validator.validateBookingAndItem(bookingId, bookingRepository, itemRepository)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));
        when(itemRepository.findById(expectedBooking.getItem().getId())).thenReturn(Optional.of(expectedItem));

        Booking actualBooking = bookingService.findBookingById(owner.getId(), bookingId);

        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void findBookingById_whenNotBookerOrOwnerRequest_thenAccessErrorExceptionTrown() {
        int bookingId = 1;

        when(validator.validateBookingAndItem(bookingId, bookingRepository, itemRepository)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));
        when(itemRepository.findById(expectedBooking.getItem().getId())).thenReturn(Optional.of(expectedItem));

        assertThrows(AccessErrorException.class,
                () -> bookingService.findBookingById(10, bookingId));
    }

    @Test
    void findBookingById_whenBookingNotFound_thenBookingNotFoundExceptionTrown() {
        int bookingId = 1;

        when(validator.validateBookingAndItem(bookingId, bookingRepository, itemRepository)).thenReturn(false);

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.findBookingById(10, bookingId));
    }

    @Test
    void findBookingByUserId_whenValidAndCaseALL_thenReturnedListOfBookings() {
        List<Booking> expectedList = new ArrayList<>();
        Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "end"));

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerId(booker.getId(), page)).thenReturn(expectedList);

        List<Booking> actualList = bookingService.findBookingByUserId(booker.getId(), "ALL", 0, 20);

        assertEquals(expectedList, actualList);
    }

    @Test
    void findBookingByUserId_whenValidAndCaseCURRENT_thenReturnedListOfBookings() {
        List<Booking> expectedList = new ArrayList<>();
        Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "end"));

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findCurrentByBookerId(booker.getId(), page)).thenReturn(expectedList);

        List<Booking> actualList = bookingService.findBookingByUserId(booker.getId(), "CURRENT", 0, 20);

        assertEquals(expectedList, actualList);
    }

    @Test
    void findBookingByUserId_whenValidAndCaseWAITING_thenReturnedListOfBookings() {
        List<Booking> expectedList = new ArrayList<>();
        Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "end"));

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatus(booker.getId(), Status.WAITING, page))
                .thenReturn(expectedList);

        List<Booking> actualList = bookingService.findBookingByUserId(booker.getId(), "WAITING", 0, 20);

        assertEquals(expectedList, actualList);
    }

    @Test
    void findBookingByUserId_whenValidAndCaseREJECTED_thenReturnedListOfBookings() {
        List<Booking> expectedList = new ArrayList<>();
        Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "end"));

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatus(booker.getId(), Status.REJECTED, page))
                .thenReturn(expectedList);

        List<Booking> actualList = bookingService.findBookingByUserId(booker.getId(), "REJECTED", 0, 20);

        assertEquals(expectedList, actualList);
    }

    @Test
    void findBookingByUserId_whenUserNotFound_thenUserNotFoundExceptionTrown() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> bookingService.findBookingByUserId(booker.getId(), "ALL", 0, 20));
    }

    @Test
    void findBookingByUserId_whenStateNotValid_thenUnsupportedStatusExceptionTrown() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));

        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.findBookingByUserId(booker.getId(), "LOL", 0, 20));
    }

    @Test
    void findBookingByUserId_whenFromOrSizeNotValid_thenValidationExceptionTrown() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));

        assertThrows(ValidationException.class,
                () -> bookingService.findBookingByUserId(booker.getId(), "ALL", 0, -20));
    }

    @Test
    void findBookingByOwnerId_whenValidAndCaseALL_thenReturnedListOfBookings() {
        List<Booking> expectedList = new ArrayList<>();
        Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "end"));

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerId(owner.getId(), page)).thenReturn(expectedList);

        List<Booking> actualList = bookingService.findBookingByOwnerId(owner.getId(), "ALL", 0, 20);

        assertEquals(expectedList, actualList);
    }

    @Test
    void findBookingByOwnerId_whenValidAndCaseCURRENT_thenReturnedListOfBookings() {
        List<Booking> expectedList = new ArrayList<>();
        Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "end"));

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findCurrentByOwnerId(owner.getId(), page)).thenReturn(expectedList);

        List<Booking> actualList = bookingService.findBookingByOwnerId(owner.getId(), "CURRENT", 0, 20);

        assertEquals(expectedList, actualList);
    }

    @Test
    void findBookingByOwnerId_whenValidAndCaseWAITING_thenReturnedListOfBookings() {
        List<Booking> expectedList = new ArrayList<>();
        Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "end"));

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findWaitingByOwnerId(owner.getId(), page)).thenReturn(expectedList);

        List<Booking> actualList = bookingService.findBookingByOwnerId(owner.getId(), "WAITING", 0, 20);

        assertEquals(expectedList, actualList);
    }

    @Test
    void findBookingByOwnerId_whenValidAndCaseREJECTED_thenReturnedListOfBookings() {
        List<Booking> expectedList = new ArrayList<>();
        Pageable page = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "end"));

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findRejectedByOwnerId(owner.getId(), page)).thenReturn(expectedList);

        List<Booking> actualList = bookingService.findBookingByOwnerId(owner.getId(), "REJECTED", 0, 20);

        assertEquals(expectedList, actualList);
    }

    @Test
    void findBookingByOwnerId_whenUserNotFound_thenUserNotFoundExceptionTrown() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> bookingService.findBookingByOwnerId(owner.getId(), "ALL", 0, 20));
    }

    @Test
    void findBookingByOwnerId_whenStateNotValid_thenUnsupportedStatusExceptionTrown() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.findBookingByOwnerId(owner.getId(), "LOL", 0, 20));
    }

    @Test
    void findBookingByOwnerId_whenFromOrSizeNotValid_thenValidationExceptionTrown() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        assertThrows(ValidationException.class,
                () -> bookingService.findBookingByOwnerId(owner.getId(), "ALL", -5, 20));
    }

}