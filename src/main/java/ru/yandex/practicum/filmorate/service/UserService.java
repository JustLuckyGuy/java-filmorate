package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.dto.new_request.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.update_request.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;


@Slf4j
@Service

public class UserService {

    private final UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(@Qualifier("UserDb") UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public List<UserDTO> getAllUsers() {
        return inMemoryUserStorage.allUser().stream().map(UserMapper::maptoUserDTO).toList();
    }

    public UserDTO getUserById(long userId){
        return inMemoryUserStorage.findById(userId).map(UserMapper::maptoUserDTO).orElseThrow(()-> new NotFoundException("Пользователь не найден"));
    }

    public UserDTO createUser(NewUserRequest request) {
        Optional<User> alreadyExistUser = inMemoryUserStorage.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            throw new DuplicateDataException("Данный email уже используется");
        }

        User user = UserMapper.mapToUser(request);
        user = inMemoryUserStorage.create(user);
        return UserMapper.maptoUserDTO(user);
    }

    public UserDTO updateUser(long userId, UpdateUserRequest request) {
        if(request.hasEmail()){
            Optional<User> alreadyExistUser = inMemoryUserStorage.findByEmail(request.getEmail());
            if (alreadyExistUser.isPresent()) {
                throw new DuplicateDataException("Данный email уже используется");
            }
        }

        User user = inMemoryUserStorage.findById(userId)
                .map(user1 -> UserMapper.updateFieldsUser(user1, request)).orElseThrow(()->new NotFoundException("Пользователь не найден"));
        user = inMemoryUserStorage.update(user);
        return UserMapper.maptoUserDTO(user);
    }

    public UserDTO addFriend(Long idUser, Long idFriend) {
        User user = inMemoryUserStorage.findById(idUser).orElseThrow(()->new NotFoundException("Пользователь с ID " + idUser + " не найден"));
        User friend = inMemoryUserStorage.findById(idFriend).orElseThrow(()->new NotFoundException("Пользователь с ID " + idFriend + " не найден"));

        if (Objects.equals(user.getId(), idFriend)) {
            throw new ValidationException("Вы не можете добавить самого себя в друзья");
        }
        inMemoryUserStorage.addFriend(user.getId(), friend.getId());
        log.trace("Пользователь {} добавил в друзья {}", user.getName(), friend.getName());
        return UserMapper.maptoUserDTO(user);
    }

    public UserDTO removeFriend(Long idUser, Long idFriend) {
        User user = inMemoryUserStorage.findById(idUser).orElseThrow(()->new NotFoundException("Пользователь с ID " + idUser + " не найден"));
        User friend = inMemoryUserStorage.findById(idFriend).orElseThrow(()->new NotFoundException("Пользователь с ID " + idFriend + " не найден"));

        inMemoryUserStorage.deleteFriend(user.getId(), friend.getId());
        log.trace("Пользователь {} удалил из друзей {}", user.getName(), friend.getName());
        return UserMapper.maptoUserDTO(user);
    }

    public List<User> showAllFriend(Long id) {
        User user = inMemoryUserStorage.findById(id).orElseThrow(()-> new NotFoundException("Пользователь не найден"));
        log.trace("Был произведен поиск всех друзей пользователя: {}", user.getName());
        return user.getFriends().stream()
                .map(inMemoryUserStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public List<UserDTO> similarFriends(Long idUser, Long idOtherUser) {

        User user = inMemoryUserStorage.findById(idUser).orElseThrow(()->new NotFoundException("Пользователь с ID " + idUser + " не найден"));
        User otherUser = inMemoryUserStorage.findById(idOtherUser).orElseThrow(()->new NotFoundException("Пользователь с ID " + idOtherUser + " не найден"));
        Set<Long> friendsOfOtherUser = otherUser.getFriends();
        log.trace("Был произведен поиск совпадающих друзей пользователя: {}", user.getName());
        return user.getFriends().stream()
                .filter(friendsOfOtherUser::contains)
                .map(inMemoryUserStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(UserMapper::maptoUserDTO)
                .toList();
    }

}
