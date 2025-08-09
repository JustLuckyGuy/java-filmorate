package ru.yandex.practicum.filmorate.storage.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private long id = 1;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> allUser() {
        return users.values();
    }

    @Override
    public User create(User user) {
        if (user.getName() == null) user.setName(user.getLogin());
        user.setId(id);
        id++;
        users.put(user.getId(), user);
        log.info("Пользователь {} создан", user.getLogin());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User update(User user) {
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
}
