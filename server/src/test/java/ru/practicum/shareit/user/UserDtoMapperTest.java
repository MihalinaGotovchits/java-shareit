package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserDtoMapperTest {
    private final UserDto dto = UserDto.builder()
            .id(1L)
            .email("user@mail.ru")
            .name("user")
            .build();
    private final User us = User.builder()
            .id(1L)
            .email("user@mail.ru")
            .name("user")
            .build();

    @Test
    public void toUserDto() {
        UserDto userDto = UserMapper.toUserDto(us);
        assertThat(userDto, equalTo(dto));
    }

    @Test
    public void toUser() {
        User user = UserMapper.toUser(dto);
        assertThat(user.getId(), equalTo(us.getId()));
        assertThat(user.getName(), equalTo(us.getName()));
        assertThat(user.getEmail(), equalTo(us.getEmail()));
    }
}