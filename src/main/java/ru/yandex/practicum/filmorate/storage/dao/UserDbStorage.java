package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("UserDb")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_ALL_FRIENDS = "SELECT friend_id FROM friendship WHERE user_id = ? AND status_friends = true";
    private static final String INSERT_USER = "INSERT INTO users(email, login, name, birthday)";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String INSERT_FRIEND = "INSERT INTO friendship(user_id, film_id) VALUES (?,?)";
    private static final String UPDATE_STATUS_FRIEND = "UPDATE friendship SET status_friends = ? WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_FRIEND = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String CHECK_STATUS = "SELECT status_friends FROM friendship WHERE user_id = ? AND film_id = ?";



    public UserDbStorage(JdbcTemplate jdbcTemplate, RowMapper<User> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public List<User> allUser() {
        List<User> users = findMany(FIND_ALL_USERS);
        for(User user: users){
            List<Long> allFriends = jdbcTemplate.queryForList(FIND_ALL_FRIENDS, Long.class, user.getId());
            user.getFriends().addAll(allFriends);
        }
        return users;
    }

    @Override
    public Optional<User> findById(Long id) {
        return findOne(FIND_USER_BY_ID, id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findOne(FIND_USER_BY_EMAIL, email);
    }

    @Override
    public User create(User user) {
        Long id = insert(INSERT_USER, "user_id", user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
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
        int row = jdbcTemplate.update(INSERT_FRIEND, userId, friendId);
        jdbcTemplate.update(UPDATE_STATUS_FRIEND, true, userId, friendId);
        return row>0;
    }

    @Override
    public boolean deleteFriend(long userId, long friendId) {
        int row = jdbcTemplate.update(DELETE_FRIEND, userId, friendId);
        jdbcTemplate.update(UPDATE_STATUS_FRIEND, false, userId, friendId);
        return row>0;
    }

    @Override
    public boolean isFriends(long userId, long friendId) {
        return checkStatus(userId, friendId) && checkStatus(friendId, userId);
    }

    private Boolean checkStatus (long userId, long friendId){
        return jdbcTemplate.queryForObject(CHECK_STATUS, Boolean.class, userId, friendId);
    }


}
