package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemByOwner(Long userId);

    ItemDto getItemById(Long itemId);

    ItemDto save(ItemDto item, Long userId);

    List<ItemDto> getItemBySearch(String text);

    ItemDto update(Long itemId, ItemDto itemDto, Long userId);
}

