package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.dto.new_request.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.new_request.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.update_request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.update_request.UpdateUserRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserDTO maptoUserDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setBirthday(user.getBirthday());
        userDTO.getFriends().addAll(user.getFriends());
        return userDTO;
    }

    public static User mapToUser(NewUserRequest request){
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .login(request.getLogin())
                .birthday(request.getBirthday())
                .build();
    }

    public static User updateFieldsUser(User user, UpdateUserRequest request){
        if(request.hasName()){
            user.setName(request.getName());
        }
        if(request.hasEmail()){
            user.setEmail(request.getEmail());
        }
        if(request.hasLogin()){
            user.setLogin(request.getLogin());
        }
        if(request.hasBirthday()){
            user.setBirthday(request.getBirthday());
        }

        return user;
    }
}
