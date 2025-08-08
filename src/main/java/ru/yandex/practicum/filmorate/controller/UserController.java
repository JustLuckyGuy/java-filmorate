package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> allUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> userFriends(@PathVariable @NotNull @Positive Long id) {
        return userService.showAllFriend(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> similarFriends(
            @PathVariable @NotNull @Positive Long id,
            @PathVariable @NotNull @Positive Long otherId) {
        return userService.similarFriends(id, otherId);
    }

    @PostMapping
    public User create(@RequestBody @Validated User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@RequestBody @Validated User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{idFriend}")
    public User addFriend(@PathVariable @NotNull @Positive Long id,
                          @PathVariable @NotNull @Positive Long idFriend) {
        return userService.addFriend(id, idFriend);
    }

    @DeleteMapping("/{id}/friends/{idFriend}")
    public User removeFriend(@PathVariable @NotNull @Positive Long id,
                             @PathVariable @NotNull @Positive Long idFriend) {
        return userService.removeFriend(id, idFriend);
    }

}
