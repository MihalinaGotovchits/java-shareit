package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {

    @Test
    void toItemRequest_ShouldMapItemRequestDtoInToItemRequest() {
        ItemRequestDtoIn requestDtoIn = new ItemRequestDtoIn();
        requestDtoIn.setDescription("Need a drill");

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDtoIn);

        assertEquals("Need a drill", itemRequest.getDescription());
    }

    @Test
    void toItemRequestDtoOut_ShouldMapItemRequestToItemRequestDtoOut() {
        User requestor = new User();
        requestor.setId(1L);

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Need a drill")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        ItemRequestDtoOut itemRequestDtoOut = ItemRequestMapper.toItemRequestDtoOut(request);

        assertEquals(1L, itemRequestDtoOut.getId());
        assertEquals("Need a drill", itemRequestDtoOut.getDescription());
        assertEquals(1L, itemRequestDtoOut.getRequestorId());
        assertEquals(request.getCreated(), itemRequestDtoOut.getCreated());
    }
}
