package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> getAllUserItems(int userId);


    ItemDto createItem(int userId, Item item);

    ItemDto changeItem(int userId, int id, ItemDto itemDto);

    ItemDto findItemById(int userId, int id);

    void removeItem(int userId, int id);

    List<ItemDto> getSearchedItems(String searchRequest);

}
