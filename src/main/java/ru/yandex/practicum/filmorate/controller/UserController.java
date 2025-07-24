package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> allUsers() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody @Validated User user) {
        if (user.getName() == null) user.setName(user.getLogin());
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь {} создан", user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Validated User user) {
        if (user.getId() == null) {
            log.error("Ошибка обновления пользователя: ID должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }

        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            oldUser.setEmail(user.getEmail());
            oldUser.setLogin(user.getLogin());
            if (!user.getName().isBlank()) oldUser.setName(user.getName());
            if (user.getBirthday() != null) oldUser.setBirthday(user.getBirthday());
            log.info("Пользователь {} обновлен успешно!", user.getLogin());
            return oldUser;
        }
        log.error("Пользователь с ID {} не найден", user.getId());
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }


    private long getNextId() {
        long currentMaxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
