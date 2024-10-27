package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

@Data
@Builder
@AllArgsConstructor
public class ItemDtoIn {
    @NotBlank(groups = {Create.class})
    @Size(max = 255, groups = {Create.class})
    private String name;

    @NotBlank(groups = Create.class)
    @Size(max = 1012, groups = {Create.class, Update.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;
}
