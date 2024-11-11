package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private final long id = 1L;
    private final UserDto userDto = UserDto.builder()
            .id(id)
            .email("user@mail.ru")
            .name("user")
            .build();
    private final User user = User.builder()
            .id(id)
            .email("user@mail.ru")
            .name("user")
            .build();

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> targetUsers = userService.getAllUsers();

        Assertions.assertNotNull(targetUsers);
        Assertions.assertEquals(1, targetUsers.size());
        verify(userRepository, times(1))
                .findAll();
    }

    @Test
    void getUserById_whenUserFound_thenReturnedUser() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto actualUser = userService.getUserById(id);

        Assertions.assertEquals(UserMapper.toUserDto(user), actualUser);
    }

    @Test
    void getUserById_whenUserNotFound_thenExceptionThrown() {
        when((userRepository).findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    void saveNewUser_whenUserNameValid_thenSavedUser() {
        when(userRepository.save(any())).thenReturn(user);

        UserDto actualUser = userService.saveUser(userDto);

        Assertions.assertEquals(userDto, actualUser);
    }

    @Test
    void saveNewUser_whenUserEmailDuplicate_thenNotSavedUser() {
        doThrow(DataIntegrityViolationException.class).when(userRepository).save(any(User.class));

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> userService.saveUser(userDto));
    }

    @Test
    void updateUser_whenUserFound_thenUpdatedOnlyAvailableFields() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto actualUser = userService.update(id, userDto);

        Assertions.assertEquals(UserMapper.toUserDto(user), actualUser);
        verify(userRepository, times(1))
                .findById(user.getId());
    }

    @Test
    void deleteUser() {
        userService.delete(1L);
        verify(userRepository, times(1))
                .deleteById(1L);
    }
}