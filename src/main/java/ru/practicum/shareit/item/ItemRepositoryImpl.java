package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;

import java.util.*;

@Slf4j
@Component
public class ItemRepositoryImpl implements ItemRepository {

    private Integer itemId = 0;

    private final Map<Integer, Map<Integer, Item>> items = new HashMap<>();

    private final Map<String, Item> itemsForSearch = new LinkedHashMap<>();

    @Override
    public List<Item> getAllItems(int userId) {
        if (items.containsKey(userId)) {
            return new ArrayList<>(items.get(userId).values());
        } else {
            log.info("Пользователь не найден или у него нет вещей");
            throw new UserNotFoundException();
        }
    }

    @Override
    public ItemDto add(int userId, Item item) {
        setItemId(getItemId() + 1);
        item.setId(getItemId());
        itemsForSearch.put(item.getName() + item.getDescription() + item.getId() + item.getOwner().getId(), item);
        Map<Integer, Item> userItems = new HashMap<>();
        if (items.containsKey(userId)) {
            userItems = items.get(userId);
            items.remove(userId);
        }
        userItems.put(item.getId(), item);
        items.put(userId, userItems);
        log.info("Вещь создана");
        return ItemMapper.itemToItemDto(item);
    }

    @Override
    public ItemDto update(int userId, int id, ItemDto itemDto) {
        Item updatedItem;
        if (items.containsKey(userId)) {
            Map<Integer, Item> userItems = items.get(userId);
            if (userItems.containsKey(id)) {
                Item item = userItems.get(id);
                if ((itemDto.getName() == null) && (itemDto.getDescription() == null)) {
                    updatedItem = updateStatus(itemDto, item);
                } else if (itemDto.getDescription() == null) {
                    updatedItem = updateName(itemDto, item);
                } else if (itemDto.getName() == null) {
                    updatedItem = updateDescription(itemDto, item);
                } else {
                    updatedItem = fullUpdateItem(itemDto, item);
                }
                if (updatedItem != null) {
                    userItems.remove(id);
                    items.remove(userId);
                    itemsForSearch.remove(item.getName() + item.getDescription() + item.getId() +
                            item.getOwner().getId());
                    itemsForSearch.put(updatedItem.getName() + updatedItem.getDescription() + updatedItem.getId() +
                            updatedItem.getOwner().getId(), updatedItem);
                    userItems.put(id, updatedItem);
                    items.put(userId, userItems);
                    log.info("Вещь обновлена");
                }
            } else {
                log.info("Вещь не найдена");
                throw new ItemNotFoundException();
            }
        } else {
            log.info("Пользователь не найден или у него нет вещей");
            throw new UserNotFoundException();
        }
        return ItemMapper.itemToItemDto(updatedItem);
    }

    @Override
    public Optional<ItemDto> findById(int userId, int id) {
        if (items.containsKey(userId)) {
            if (items.get(userId).containsKey(id)) {
                return Optional.of(ItemMapper.itemToItemDto(items.get(userId).get(id)));
            } else {
                log.info("Вещь с id = " + id + " не найдена");
                return Optional.empty();
            }
        } else {
            log.info("Пользователь не найден или у пользователя нет вещей");
            return Optional.empty();
        }
    }

    @Override
    public void delete(int userId, int id) {
        if (items.containsKey(userId)) {
            Map<Integer, Item> userItems = items.get(userId);
            if (userItems.containsKey(id)) {
                userItems.remove(id);
                log.info("Вещь с id = " + id + " удалена");
            } else {
                log.info("Вещь с id = " + id + " не найдена");
                throw new ItemNotFoundException();
            }
        } else {
            log.info("Пользователь не найден или у пользователя нет вещей");
            throw new UserNotFoundException();
        }
    }

    @Override
    public List<ItemDto> findItems(String searchRequest) {
        List<ItemDto> searchedItems = new ArrayList<>();
        for (String itemDescription : itemsForSearch.keySet()) {
            if (itemDescription.toLowerCase().contains(searchRequest.toLowerCase())) {
                Item item = itemsForSearch.get(itemDescription);
                if (item.getAvailable()) {
                    searchedItems.add(ItemMapper.itemToItemDto(item));
                }
            }
        }
        return searchedItems;
    }

    private Item updateName(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(item.getId())
                .name(itemDto.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .build();
    }

    private Item updateDescription(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(itemDto.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .build();
    }

    private Item updateStatus(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(itemDto.getAvailable())
                .owner(item.getOwner())
                .build();
    }

    private Item fullUpdateItem(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(item.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(item.getOwner())
                .build();
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

}
