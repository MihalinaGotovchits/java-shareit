package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.ItemRequest;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest toItemRequest(ItemRequestDtoIn requestDtoIn) {
        return ItemRequest.builder()
                .description(requestDtoIn.getDescription())
                .build();
    }

    public ItemRequestDtoOut toItemRequestDtoOut(ItemRequest request) {
        return ItemRequestDtoOut.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestorId(request.getRequestor().getId())
                .created(request.getCreated())
                .build();
    }
}