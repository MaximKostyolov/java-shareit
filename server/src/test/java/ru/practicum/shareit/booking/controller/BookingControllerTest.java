package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceJpaImpl;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingServiceJpaImpl bookingService;

    @SneakyThrows
    @Test
    void create() {
        int userId = 1;
        BookingDto bookingDto = new BookingDto(1,
                LocalDateTime.of(2033, 9, 5, 12, 0),
                LocalDateTime.of(2033, 10, 5, 12, 0),
                1, "ItemName", 1, Status.WAITING);

        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService).createBooking(userId, bookingDto);
    }

    @SneakyThrows
    @Test
    void change() {
        int userId = 1;
        int bookingId = 1;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService).approvedBooking(userId, bookingId, true);
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        int userId = 1;
        int bookingId = 1;

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService).findBookingById(userId, bookingId);
    }

    @SneakyThrows
    @Test
    void getBookingsByUserId() {
        int userId = 1;

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService).findBookingByUserId(userId, "ALL", 0, 20);
    }

    @SneakyThrows
    @Test
    void getBookingsByOwnerId() {
        int userId = 1;

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService).findBookingByOwnerId(userId, "ALL", 0, 20);
    }

}