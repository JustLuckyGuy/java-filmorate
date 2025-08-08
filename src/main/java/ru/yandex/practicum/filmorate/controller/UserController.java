package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
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
    public Collection<User> userFriends(@PathVariable Long id) {
        if (id == null) {
            log.error("Необходимо указать id");
            throw new ParameterNotValidException("Не указан id");
        }
        return userService.showAllFriend(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> similarFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) {
        if (id == null || otherId == null) {
            log.error("Не введен id для одного из полей");
            throw new ParameterNotValidException("Проверьте правильность ввода id пользователей и id второго пользователя");
        }

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
    public User addFriend(@PathVariable Long id,
                          @PathVariable Long idFriend) {
        if (id == null || idFriend == null) {
            log.error("Добавить в друзья: не введен один из полей id");
            throw new ParameterNotValidException("Проверьте правильность ввода id пользователей и id друга");
        }
        return userService.addFriend(id, idFriend);
    }

    @DeleteMapping("/{id}/friends/{idFriend}")
    public User removeFriend(@PathVariable Long id,
                             @PathVariable Long idFriend) {
        if (id == null || idFriend == null) {
            log.error("Удаление из друзья: не введен один из полей id");
            throw new ParameterNotValidException("Проверьте правильность ввода id пользователей");
        }

        return userService.removeFriend(id, idFriend);
    }

}
