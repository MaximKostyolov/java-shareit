package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private Integer requestId;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;

}