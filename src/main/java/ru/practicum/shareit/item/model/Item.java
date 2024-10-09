package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Long requestId;
    private Boolean available;
}
