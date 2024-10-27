package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;

public interface ItemService {
    List<ItemDtoOut> getItemByOwner(Long userId);

    ItemDtoOut getItemById(Long itemId, Long userId);

    ItemDtoOut save(ItemDtoIn itemDtoIn, Long userId);

    CommentDtoOut saveComment(Long itemId, CommentDtoIn commentDtoIn, Long userId);

    List<ItemDtoOut> getItemBySearch(String text);

    ItemDtoOut update(Long itemId, ItemDtoIn itemDtoIn, Long userId);
}

