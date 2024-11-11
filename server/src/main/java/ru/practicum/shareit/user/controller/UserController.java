package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("GET / users");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("GET / users / {}", userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto saveUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("POST / users / {}", userDto.getName());
        return userService.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("PATCH / users / {}", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("DELETE / users / {}", userId);
        userService.delete(userId);
    }
}

