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
import ru.yandex.practicum.filmorate.model.FeedBlock;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.Optional;


@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(@Qualifier("userdb") UserStorage userStorage, @Qualifier("filmdb") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public List<UserDTO> getAllUsers() {
        log.info("Выгрузка всех пользователей");
        return userStorage.allUser().stream().map(UserMapper::maptoUserDTO).toList();
    }

    public UserDTO getUserById(long userId) {
        log.info("Выгрузка пользователя {}", userId);
        return userStorage.findById(userId).map(UserMapper::maptoUserDTO).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public UserDTO createUser(NewUserRequest request) {
        log.trace("Произведена проверка на email пользователя {}", request.getLogin());
        Optional<User> alreadyExistUser = userStorage.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            throw new DuplicateDataException("Данный email уже используется");
        }
        User user = UserMapper.mapToUser(request);
        log.trace("Сохранение в базу данных нового пользователя {}", user.getLogin());
        user = userStorage.create(user);
        log.info(user.getLogin());
        return UserMapper.maptoUserDTO(checkUser(user.getId()));
    }

    public UserDTO updateUser(UpdateUserRequest request) {
        if (request.hasEmail()) {
            log.trace("Пользователь {} пытается поменять email ", request.getLogin());
            Optional<User> alreadyExistUser = userStorage.findByEmail(request.getEmail());
            if (alreadyExistUser.isPresent()) {
                throw new DuplicateDataException("Данный email уже используется");
            }
        }

        User user = userStorage.findById(request.getId()).map(user1 -> UserMapper.updateFieldsUser(user1, request)).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.trace("Пользователь обновлен");
        user = userStorage.update(user);
        return UserMapper.maptoUserDTO(user);
    }

    public boolean removeUser(long id) {
        User user = checkUser(id);
        log.info("Попытка удаления пользователя");
        return userStorage.delete(user.getId());
    }

    public UserDTO addFriend(Long idUser, Long idFriend) {
        User user = checkUser(idUser);
        User friend = checkUser(idFriend);

        if (Objects.equals(user.getId(), idFriend)) {
            throw new ValidationException("Вы не можете добавить самого себя в друзья");
        }

        if (userStorage.addFriend(user.getId(), friend.getId())) user.getFriends().add(friend.getId());

        log.trace("Пользователь {} добавил в друзья {}", user.getName(), friend.getName());
        return UserMapper.maptoUserDTO(checkUser(user.getId()));
    }

    public UserDTO removeFriend(Long idUser, Long idFriend) {
        User user = checkUser(idUser);
        User friend = checkUser(idFriend);

        userStorage.deleteFriend(user.getId(), friend.getId());
        log.trace("Пользователь {} удалил из друзей {}", user.getName(), friend.getName());
        return UserMapper.maptoUserDTO(checkUser(user.getId()));
    }

    public List<UserDTO> showAllFriend(Long id) {
        User user = checkUser(id);
        log.trace("Был произведен поиск всех друзей пользователя: {}", user.getName());
        return userStorage.findFriends(user.getId()).stream().map(UserMapper::maptoUserDTO).toList();
    }

    public List<UserDTO> similarFriends(Long idUser, Long idOtherUser) {
        User user = checkUser(idUser);
        User otherUser = checkUser(idOtherUser);
        List<User> commonFriends = userStorage.confirmedFriends(user.getId(), otherUser.getId());

        log.trace("Был произведен поиск совпадающих друзей пользователя: {}", user.getName());
        return commonFriends.stream().map(UserMapper::maptoUserDTO).toList();
    }

    public List<FeedBlock> findUserFeed(Long userId) {
        checkUser(userId);
        return userStorage.findUserFeed(userId);
    }

    private User checkUser(Long userId) {
        return userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    public List<FilmDTO> getRecommendations(Long userId) {
        checkUser(userId);
        List<Long> filmIds = userStorage.getRecommendations(userId);
        return filmIds.isEmpty() ? Collections.emptyList() : convertToFilmDTOs(filmIds);
    }

    private List<FilmDTO> convertToFilmDTOs(List<Long> filmIds) {
        return filmIds.stream()
                .map(this::findFilmById)
                .filter(Objects::nonNull)
                .toList();
    }

    private FilmDTO findFilmById(Long filmId) {
        return filmStorage.findById(filmId)
                .map(FilmMapper::maptoFilmDTO)
                .orElse(null);
    }
}
