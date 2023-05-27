package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getUsersItems(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all users items userId={}, from={}, size={}", userId, from, size);
        return itemClient.getUsersItems(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        if ((itemDto.getName() != null) && (itemDto.getDescription() != null)) {
            return itemClient.createItem(userId, itemDto);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Некорректный запрос. ItemName и ItemDescription не могут быть пустыми");
        }
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Update itemId={}  userId={}, itemDto={}", itemId, userId, itemDto);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long itemId) {
        log.info("Get item itemId={}, userId={}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId) {
        log.info("Delete item itemId={}, userId={}", itemId, userId);
        return itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchedItem(@RequestHeader("X-Sharer-User-Id") long userId,
                        @RequestParam(name = "text") String searchRequest,
                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items searchRequest={}, from={}, size={}", searchRequest, from, size);
        return itemClient.getSearchedItem(userId, searchRequest, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long itemId,
                                                @RequestBody @Valid Comment comment) {
        log.info("Creating comment {}, itemId={}, userId={}", comment, itemId, userId);
        return itemClient.createComment(comment, itemId, userId);
    }

}