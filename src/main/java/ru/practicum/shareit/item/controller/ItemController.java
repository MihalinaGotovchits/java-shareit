package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
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
    public ItemDtoOut getItem(@PathVariable Long itemId, @RequestHeader(XSHARER) Long userId) {
        log.info("GET / items {} / user {}", itemId, userId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoOut> getItemsByOwner(@RequestHeader(XSHARER) Long userId) {
        log.info("GET / items");
        return itemService.getItemByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDtoOut> getItemsBySearch(@RequestParam String text) {
        log.info("GET / search / {}", text);
        return itemService.getItemBySearch(text);
    }

    @PostMapping
    public ItemDtoOut save(@RequestBody @Validated(Create.class) ItemDtoIn itemDtoIn,
                           @RequestHeader(XSHARER) Long userId) {
        log.info("POST / items / {}, {}", itemDtoIn.getName(), userId);
        return itemService.save(itemDtoIn, userId);
    }

    @PatchMapping("{itemId}")
    public ItemDtoOut update(@PathVariable Long itemId, @RequestBody ItemDtoIn itemDtoIn,
                             @RequestHeader(XSHARER) Long userId) {
        log.info("PATCH / items / {}", itemId);
        return itemService.update(itemId, itemDtoIn, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut addComment(@PathVariable Long itemId,
                                    @Validated(Create.class) @RequestBody CommentDtoIn commentDtoIn,
                                    @RequestHeader(XSHARER) Long userId) {
        log.info("POST / comment / item {}", itemId);
        return itemService.saveComment(itemId, commentDtoIn, userId);
    }
}

