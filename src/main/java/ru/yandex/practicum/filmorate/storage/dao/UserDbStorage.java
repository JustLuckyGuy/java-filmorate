package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;


@Slf4j
@Repository
@Qualifier("userdb")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_ALL_FRIENDS = "SELECT friend_id FROM friendship WHERE user_id = ? AND status_friends = true";
    private static final String FIND_COMMON_FRIENDS = "SELECT f1.friend_id " +
            "FROM friendship f1 " +
            "JOIN friendship f2 ON f1.friend_id = f2.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id = ? " +
            "AND f1.status_friends = true AND f2.status_friends = true";
    private static final String INSERT_USER = "INSERT INTO users(email, login, name, birthday) VALUES(?,?,?,?)";
    private static final String INSERT_FRIEND = "INSERT INTO friendship(user_id, friend_id) VALUES(?,?)";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String DELETE_FRIEND = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";

    public UserDbStorage(JdbcTemplate jdbcTemplate, RowMapper<User> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public List<User> allUser() {
        List<User> users = findMany(FIND_ALL_USERS);
        for (User user : users) {
            loadFriends(user);
        }
        return users;
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<User> user = findOne(FIND_USER_BY_ID, id);
        user.ifPresent(this::loadFriends);
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findOne(FIND_USER_BY_EMAIL, email);
    }

    @Override
    public User create(User user) {
        Long id = insert(INSERT_USER, "user_id", user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
        log.info(user.getLogin());
        return user;
    }

    @Override
    public User update(User user) {
        update(UPDATE_USER, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public boolean delete(long userId) {
        return delete(DELETE_USER, userId);
    }


    @Override
    public boolean addFriend(long userId, long friendId) {
        try {
            int row = jdbcTemplate.update(INSERT_FRIEND, userId, friendId);
            return row > 0;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Override
    public boolean deleteFriend(long userId, long friendId) {
        int row = jdbcTemplate.update(DELETE_FRIEND, userId, friendId);
        return row > 0;
    }


    public List<Long> confirmedFriends(long userId, long otherId) {
        return jdbcTemplate.queryForList(FIND_COMMON_FRIENDS, Long.class, userId, otherId);
    }


    private void loadFriends(User user) {
        log.trace("Загружаются все друзья пользователя {}", user.getName());
        List<Long> friends = jdbcTemplate.queryForList(FIND_ALL_FRIENDS, Long.class, user.getId());
        user.getFriends().clear();
        user.getFriends().addAll(friends);
    }

}
