package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final InMemoryUserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> allUsers() {
        return userStorage.allUser();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> userFriends(@PathVariable Long id) {
        if (id == null) {
            throw new ParameterNotValidException("Не указан id");
        }
        return userService.showAllFriend(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> similarFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) {
        if (id == null || otherId == null) {
            throw new ParameterNotValidException("Проверьте правильность ввода id пользователей и id второго пользователя");
        }

        return userService.similarFriends(id, otherId);
    }

    @PostMapping
    public User create(@RequestBody @Validated User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Validated User user) {
        return userStorage.update(user);
    }

    @PutMapping("/{id}/friends/{idFriend}")
    public User addFriend(@PathVariable Long id,
                          @PathVariable Long idFriend) {
        if (id == null || idFriend == null) {
            throw new ParameterNotValidException("Проверьте правильность ввода id пользователей и id друга");
        }

        return userService.addFriend(id, idFriend);
    }

    @DeleteMapping("/{id}/friends/{idFriend}")
    public User removeFriend(@PathVariable Long id,
                             @PathVariable Long idFriend) {
        if (id == null || idFriend == null) {
            throw new ParameterNotValidException("Проверьте правильность ввода id пользователей");
        }

        return userService.removeFriend(id, idFriend);
    }

}
