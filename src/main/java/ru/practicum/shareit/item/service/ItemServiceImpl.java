package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> getItemByOwner(Long userId) {
        userService.getUserById(userId);
        return itemRepository.getItemByOwner(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.getItemById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с ID " + itemId + "не найдена"));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto save(ItemDto itemDto, Long userId) {
        userService.getUserById(userId);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto), userId));
    }

    @Override
    public List<ItemDto> getItemBySearch(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.getItemBySearch(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
        userService.getUserById(userId);
        Item item = itemRepository.getItemById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с ID " + itemId + "не найдена"));
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();
        if (item.getOwnerId().equals(userId)) {
            if (name != null && !name.isBlank()) {
                item.setName(name);
            }
            if (description != null && !description.isBlank()) {
                item.setDescription(description);
            }
            if (available != null) {
                item.setAvailable(available);
            }
        } else {
            throw new NotOwnerException("Пользователь с ID " + userId + " не является владельцем " + name);
        }
        return ItemMapper.toItemDto(item);
    }
}

