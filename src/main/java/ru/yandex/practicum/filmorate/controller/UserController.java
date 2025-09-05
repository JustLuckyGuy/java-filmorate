package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.dto.new_request.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.update_request.UpdateUserRequest;
import ru.yandex.practicum.filmorate.service.UserService;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDTO> allUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable @NotNull @Positive long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDTO> userFriends(@PathVariable @NotNull @Positive Long id) {
        return userService.showAllFriend(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDTO> similarFriends(
            @PathVariable @NotNull @Positive Long id,
            @PathVariable @NotNull @Positive Long otherId) {
        return userService.similarFriends(id, otherId);
    }

    @PostMapping
    public UserDTO create(@RequestBody @Validated NewUserRequest user) {
        return userService.createUser(user);
    }

    @PutMapping
    public UserDTO update(@RequestBody @Validated UpdateUserRequest user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{idFriend}")
    public UserDTO addFriend(@PathVariable @NotNull @Positive Long id,
                             @PathVariable @NotNull @Positive Long idFriend) {
        return userService.addFriend(id, idFriend);
    }

    @DeleteMapping("/{id}/friends/{idFriend}")
    public UserDTO removeFriend(@PathVariable @NotNull @Positive Long id,
                                @PathVariable @NotNull @Positive Long idFriend) {
        return userService.removeFriend(id, idFriend);
    }

    @DeleteMapping("/{id}")
    public boolean removeUser(@PathVariable @NotNull @Positive Long id) {
        return userService.removeUser(id);
    }

}
