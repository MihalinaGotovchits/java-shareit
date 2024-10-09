package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> getItemByOwner(Long userId);

    Optional<Item> getItemById(Long itemId);

    Item save(Item item, Long userId);

    List<Item> getItemBySearch(String text);
}

