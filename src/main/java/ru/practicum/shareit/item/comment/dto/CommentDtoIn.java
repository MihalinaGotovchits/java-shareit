package ru.practicum.shareit.item.comment.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

@Data
@Builder
@AllArgsConstructor
public class CommentDtoIn {
    Long id;

    @Size(max = 1000, groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    private String text;
}
