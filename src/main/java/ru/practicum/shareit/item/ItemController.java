package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> getAllUserItems(@RequestHeader("X-Sharer-User-Id") int userId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemService.getAllUserItems(userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody Item item,
                          HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if (userId > 0) {
            return itemService.createItem(userId, item);
        } else {
            log.info("Некорректный запрос. userId должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Некорректный запрос. userId должен быть больше 0");
        }
    }

    @PatchMapping("/{itemId}")
    public ItemDto change(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId,
                          @RequestBody ItemDto itemDto, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if (userId > 0) {
            return itemService.changeItem(userId, itemId, itemDto);
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Некорректный запрос. Id должен быть больше 0");
        }
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId,
                           HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemService.findItemById(userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId,
                           HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        itemService.removeItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchedItems(@RequestParam("text") String searchRequest) {
        if (!searchRequest.isEmpty()) {
            return itemService.getSearchedItems(searchRequest);
        } else {
            return new ArrayList<>();
        }
    }

}
