package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage inMemoryUserStorage;


    public User addFriend(Long idUser, Long idFriend) {
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
        User user = searchUser(idUser);
        User friend = searchUser(idFriend);
        user.getFriends().remove(idFriend);
        friend.getFriends().remove(idUser);
        log.trace("Пользователь {} удалил из друзей {}", user.getName(), friend.getName());
        return user;
    }

    public List<User> showAllFriend(Long id) {
        User user = searchUser(id);
        log.trace("Был произведен поиск всех друзей пользователя: {}", user.getName());
        return user.getFriends().stream()
                .map(inMemoryUserStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public List<User> similarFriends(Long idUser, Long idOtherUser) {

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
        Optional<User> user = inMemoryUserStorage.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + id + " не найден");

        }
        return user.get();
    }


}
