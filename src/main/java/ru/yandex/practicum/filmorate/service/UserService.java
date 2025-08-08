package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage inMemoryUserStorage;

    public Collection<User> getAllUsers() {
        return inMemoryUserStorage.allUser();
    }

    public User createUser(User user) {
        return inMemoryUserStorage.create(user);
    }

    public User updateUser(User user) {
        return inMemoryUserStorage.update(user);
    }

    public User addFriend(Long idUser, Long idFriend) {
        if (idUser <= 0 || idFriend <= 0) {
            log.error("Добавить в друзья: ID не должен быть отрицательным");
            throw new ParameterNotValidException("Не верно указан id пользователя или id друга");
        }
        User user = searchUser(idUser);
        User friend = searchUser(idFriend);
        if (Objects.equals(user.getId(), idFriend)) {
            throw new ValidationException("Вы не можете добавить самого себя в друзья");
        }
        user.getFriends().add(idFriend);
        friend.getFriends().add(idUser);
        log.trace("Пользователь {} добавил в друзья {}", user.getName(), friend.getName());
        return user;
    }

    public User removeFriend(Long idUser, Long idFriend) {
        if (idUser <= 0 || idFriend <= 0) {
            log.error("Удаление из друзья: ID не должен быть отрицательным");
            throw new ParameterNotValidException("Не верно указан id пользователя или id друга");
        }
        User user = searchUser(idUser);
        User friend = searchUser(idFriend);
        user.getFriends().remove(idFriend);
        friend.getFriends().remove(idUser);
        log.trace("Пользователь {} удалил из друзей {}", user.getName(), friend.getName());
        return user;
    }

    public List<User> showAllFriend(Long id) {
        if (id <= 0) {
            log.error("Id не должно быть отрицательным");
            throw new ParameterNotValidException("Не верно указан id");
        }
        User user = searchUser(id);
        log.trace("Был произведен поиск всех друзей пользователя: {}", user.getName());
        return user.getFriends().stream()
                .map(inMemoryUserStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public List<User> similarFriends(Long idUser, Long idOtherUser) {
        if (idUser <= 0 || idOtherUser <= 0) {
            log.error("Id отрицательное, а должно быть положительным");
            throw new ParameterNotValidException("Не верно указан id одного из пользователей");
        }

        User user = searchUser(idUser);
        Set<Long> friendsOfOtherUser = searchUser(idOtherUser).getFriends();
        log.trace("Был произведен поиск совпадающих друзей пользователя: {}", user.getName());
        return user.getFriends().stream()
                .filter(friendsOfOtherUser::contains)
                .map(inMemoryUserStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private User searchUser(Long id) {
        return inMemoryUserStorage.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователь c id = " + id + " не найден"));
    }


}
