package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.dto.new_request.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.update_request.UpdateUserRequest;
import ru.yandex.practicum.filmorate.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserDTO maptoUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setLogin(user.getLogin());
        userDTO.setBirthday(user.getBirthday());
        userDTO.getFriends().addAll(user.getFriends());
        return userDTO;
    }

    public static User mapToUser(NewUserRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .login(request.getLogin())
                .birthday(request.getBirthday())
                .build();
        if (request.getName() == null || request.getName().isBlank()) {
            user.setName(request.getLogin());
        } else {
            user.setName(request.getName());
        }
        return user;
    }

    public static User updateFieldsUser(User user, UpdateUserRequest request) {
        if (request.hasName()) {
            user.setName(request.getName());
        }
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
        if (request.hasLogin()) {
            user.setLogin(request.getLogin());
        }
        if (request.hasBirthday()) {
            user.setBirthday(request.getBirthday());
        }

        return user;
    }
}
