package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dto.update_request.UpdateUserRequest;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.row_mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbTest {
    private final UserDbStorage userDbStorage;

    private User testUser;
    private User testFriend;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@mail.ru")
                .login("testLogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        testFriend = User.builder()
                .email("friend@mail.ru")
                .login("friendLogin")
                .name("Friend User")
                .birthday(LocalDate.of(1992, 2, 2))
                .build();
    }

    @Test
    void testFindUserById() {
        User createdUser = userDbStorage.create(testUser);
        Optional<User> foundUser = userDbStorage.findById(createdUser.getId());

        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isEqualTo(createdUser.getId());
                    assertThat(user.getEmail()).isEqualTo("test@mail.ru");
                });
    }

    @Test
    void testFindAllUsers() {
        userDbStorage.create(testUser);
        List<User> users = userDbStorage.allUser();

        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(1);
    }

    @Test
    void testFindUserByEmail() {
        User createdUser = userDbStorage.create(testUser);
        Optional<User> foundUser = userDbStorage.findByEmail("test@mail.ru");

        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isEqualTo(createdUser.getId());
                    assertThat(user.getEmail()).isEqualTo("test@mail.ru");
                });
    }

    @Test
    void testCreateUser() {
        User createdUser = userDbStorage.create(testUser);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("test@mail.ru");
    }

    @Test
    void testUpdateUser() {
        User createdUser = userDbStorage.create(testUser);
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Updated Name");
        updateUserRequest.setEmail("updated@mail.ru");

        User result = userDbStorage.update(UserMapper.updateFieldsUser(createdUser, updateUserRequest));

        assertThat(result.getEmail()).isEqualTo("updated@mail.ru");
        assertThat(result.getName()).isEqualTo("Updated Name");
    }

    @Test
    void testDeleteUser() {
        User createdUser = userDbStorage.create(testUser);
        boolean isDeleted = userDbStorage.delete(createdUser.getId());

        assertThat(isDeleted).isTrue();
        assertThat(userDbStorage.findById(createdUser.getId())).isEmpty();
    }

    @Test
    void testAddAndDeleteFriend() {
        User user1 = userDbStorage.create(testUser);
        User user2 = userDbStorage.create(testFriend);

        boolean friendAdded = userDbStorage.addFriend(user1.getId(), user2.getId());
        boolean friendDeleted = userDbStorage.deleteFriend(user1.getId(), user2.getId());

        assertThat(friendAdded).isTrue();
        assertThat(friendDeleted).isTrue();
    }

    @Test
    void testFindCommonFriends() {
        User user1 = userDbStorage.create(testUser);
        User user2 = userDbStorage.create(testFriend);

        User commonFriend = User.builder()
                .email("commonFriend@mail.ru")
                .login("commonFriendLogin")
                .name("Common Friend")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        User createdCommonFriend = userDbStorage.create(commonFriend);


        userDbStorage.addFriend(user1.getId(), createdCommonFriend.getId());
        userDbStorage.addFriend(user2.getId(), createdCommonFriend.getId());

        List<Long> commonFriends = userDbStorage.confirmedFriends(user1.getId(), user2.getId());

        assertThat(commonFriends).isNotNull();
        assertThat(commonFriends.size()).isEqualTo(1);
        assertThat(commonFriends.getFirst()).isEqualTo(createdCommonFriend.getId());
    }
}
