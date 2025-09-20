package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FeedBlock;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.row_mappers.FeedBlockRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Slf4j
@Repository
@Qualifier("userdb")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_COMMON_FRIENDS = " select * from users u, friendship f, friendship o " + "where u.user_id = f.friend_id AND u.user_id = o.friend_id AND f.user_id = ? AND o.user_id = ?";
    private static final String INSERT_USER = "INSERT INTO users(email, login, name, birthday) VALUES(?,?,?,?)";
    private static final String INSERT_FRIEND = "INSERT INTO friendship(user_id, friend_id) VALUES(?,?)";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String DELETE_FRIEND = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_FRIEND_OF_USER = "SELECT u.* FROM users u " + "JOIN friendship f ON u.user_id = f.friend_id WHERE f.user_id = ? AND f.status_friends = true";
    private static final String FIND_USER_FEED = "SELECT * FROM feed WHERE user_id = ? ";
    private static final String FIND_USER_RECOMMENDATIONS = """
            WITH user_likes AS (
                SELECT film_id FROM likes WHERE user_id = ?
            ),
            other_users_likes AS (
                SELECT l.user_id, l.film_id
                FROM likes l
                WHERE l.user_id != ? AND l.film_id IN (SELECT film_id FROM user_likes)
            ),
            similarity_scores AS (
                SELECT
                    oul.user_id,
                    COUNT(*) as common_likes
                FROM other_users_likes oul
                GROUP BY oul.user_id
                ORDER BY common_likes DESC
                LIMIT 1
            ),
            recommendations AS (
                SELECT DISTINCT l.film_id
                FROM likes l
                JOIN similarity_scores ss ON l.user_id = ss.user_id
                WHERE l.film_id NOT IN (SELECT film_id FROM user_likes)
            )
            SELECT film_id FROM recommendations
            """;

    public UserDbStorage(JdbcTemplate jdbcTemplate, RowMapper<User> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public List<User> allUser() {
        return findMany(FIND_ALL_USERS);
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
            update(INSERT_FEED, userId, "FRIEND", "ADD", friendId, Timestamp.from(Instant.now()));
            return row > 0;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Override
    public boolean deleteFriend(long userId, long friendId) {
        int row = jdbcTemplate.update(DELETE_FRIEND, userId, friendId);
        if (row > 0) {
            update(INSERT_FEED, userId, "FRIEND", "REMOVE", friendId, Timestamp.from(Instant.now()));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<User> findFriends(long id) {
        return findMany(FIND_FRIEND_OF_USER, id);
    }

    @Override
    public List<User> confirmedFriends(long userId, long otherId) {
        return findMany(FIND_COMMON_FRIENDS, userId, otherId);
    }

    @Override
    public List<FeedBlock> findUserFeed(Long userId) {
        return jdbcTemplate.query(FIND_USER_FEED, new FeedBlockRowMapper(), userId);
    }

    @Override
    public List<Long> getRecommendations(Long userId) {
        return jdbcTemplate.query(FIND_USER_RECOMMENDATIONS, (rs, rowNum) -> rs.getLong("film_id"), userId, userId);
    }
}
