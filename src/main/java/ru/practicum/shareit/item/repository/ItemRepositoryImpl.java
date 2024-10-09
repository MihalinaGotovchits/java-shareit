package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static Long generatorId = 0L;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> userItems = new HashMap<>();

    @Override
    public List<Item> getItemByOwner(Long userId) {
        return userItems.get(userId);
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item save(Item item, Long userId) {
        item.setId(++generatorId);
        item.setOwnerId(userId);
        items.put(item.getId(), item);
        final List<Item> itemsByOwner = userItems.computeIfAbsent(item.getOwnerId(), k -> new ArrayList<>());
        itemsByOwner.add(item);
        return item;
    }

    @Override
    public List<Item> getItemBySearch(String text) {
        return items.values().stream()
                .filter(item -> item.getAvailable() && ((item.getName().toLowerCase().contains(text.toLowerCase())) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }
}

