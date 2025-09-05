package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> allUser();

    User create(User user);

    User update(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean addFriend(long userId, long friendId);

    boolean delete(long userId);

    boolean deleteFriend(long userId, long friendId);


    List<Long> confirmedFriends(long userId, long friendId);
}
