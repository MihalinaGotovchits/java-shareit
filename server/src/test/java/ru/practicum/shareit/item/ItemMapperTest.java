package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemMapperTest {
    private final ItemDtoIn itemDtoIn = ItemDtoIn.builder()
            .name("Item")
            .description("Nice item")
            .requestId(null)
            .available(true)
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("Item")
            .description("Nice item")
            .available(true)
            .request(null)
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Item")
            .build();

    @Test
    public void toItemDto() {
        ItemDto itemDto1 = ItemMapper.toItemDto(item);
        assertThat(itemDto1, equalTo(itemDto));
    }

    @Test
    public void toItem() {
        Item item1 = ItemMapper.toItem(itemDtoIn);
        assertThat(item1.getName(), equalTo(item.getName()));
        assertThat(item1.getDescription(), equalTo(item.getDescription()));
        assertThat(item1.getAvailable(), equalTo(item.getAvailable()));
        assertThat(item1.getRequest(), equalTo(item.getRequest()));
    }
}
