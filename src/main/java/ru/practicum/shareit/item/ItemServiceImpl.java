package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Item> getAllUserItems(int userId) {
        return itemRepository.getAllItems(userId);
    }

    @Override
    public ItemDto createItem(int userId, Item item) {
        item.setOwner(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException()));
        return itemRepository.add(userId, item);
    }

    @Override
    public ItemDto changeItem(int userId, int id, ItemDto itemDto) {
        return itemRepository.update(userId, id, itemDto);
    }

    @Override
    public ItemDto findItemById(int userId, int id) {
        return itemRepository.findById(userId, id).orElseThrow(() -> new ItemNotFoundException());
    }

    @Override
    public void removeItem(int userId, int id) {
        itemRepository.delete(userId, id);
    }

    @Override
    public List<ItemDto> getSearchedItems(String searchRequest) {
        return itemRepository.findItems(searchRequest);
    }
}
