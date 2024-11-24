package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class ItemServiceIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void saveNewItem() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("user@mail.ru")
                .name("user")
                .build();

        // Сохраняем пользователя
        UserDto user = userService.saveUser(userDto);

        ItemDtoIn itemDtoIn = ItemDtoIn.builder()
                .name("item")
                .description("nice item")
                .available(true)
                .requestId(null)
                .build();

        itemService.save(itemDtoIn, user.getId());

        var item = em.createQuery("Select i from Item i where i.name = :itemName", Item.class)
                .setParameter("itemName", itemDtoIn.getName())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDtoIn.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoIn.getDescription()));
    }
}
