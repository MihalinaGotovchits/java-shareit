package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.utils.Create;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    private Long id;

    @NotNull(groups = {Create.class})
    private long itemId;

    @FutureOrPresent(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private LocalDateTime start;

    @Future(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private LocalDateTime end;
}
