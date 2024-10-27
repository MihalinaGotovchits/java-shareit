package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
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
public class UserDto {
    private Long id;

    @NotBlank(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    @Size(max = 512, groups = {Create.class, Update.class})
    private String email;

    @NotBlank(groups = {Create.class})
    @Size(max = 255, groups = {Create.class, Update.class})
    private String name;
}

