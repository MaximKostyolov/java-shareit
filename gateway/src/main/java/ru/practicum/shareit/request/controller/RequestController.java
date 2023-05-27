package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestBody @Valid RequestDto requestDto) {
        log.info("Creating itemRequest {}, userId={}", requestDto, userId);
        return requestClient.createItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsersRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get all users itemRequests, userId={}", userId);
        return requestClient.getAllUsersRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get other itemRequests, userId={}, from={}, size={}", userId, from, size);
        return requestClient.getOtherRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long requestId) {
        log.info("Get itemRequest requestId={}, userId={}", requestId, userId);
        return requestClient.getItemRequest(requestId, userId);
    }

}