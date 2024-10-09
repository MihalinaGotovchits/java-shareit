package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.Create;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String XSHARER = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("GET / items / {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(XSHARER) Long userId) {
        log.info("GET / items");
        return itemService.getItemByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(@RequestParam String text) {
        log.info("GET / search / {}", text);
        return itemService.getItemBySearch(text);
    }

    @PostMapping
    public ItemDto save(@Validated(Create.class) @RequestBody ItemDto itemDto,
                        @RequestHeader(XSHARER) Long userId) {
        log.info("POST / items / {}, {}", itemDto.getName(), userId);
        return itemService.save(itemDto, userId);
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                          @RequestHeader(XSHARER) Long userId) {
        log.info("PATCH / items / {}", itemId);
        return itemService.update(itemId, itemDto, userId);
    }
}

