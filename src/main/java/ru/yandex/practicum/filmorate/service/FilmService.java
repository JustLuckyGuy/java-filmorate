package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.new_request.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.update_request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortOrder;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;


@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmDb;
    private final UserStorage userStorage;


    @Autowired
    public FilmService(@Qualifier("filmdb") FilmStorage filmDb, @Qualifier("userdb") UserStorage userStorage) {
        this.filmDb = filmDb;
        this.userStorage = userStorage;
    }

    public List<FilmDTO> getAllFilms() {
        log.trace("Произведен вызов всех фильмов из базы данных");
        return filmDb.allFilms().stream().map(FilmMapper::maptoFilmDTO).toList();
    }

    public List<FilmDTO> getFilmsDirector(Long directorId, String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }
        return filmDb.allFilmsOfDirector(directorId, SortOrder.from(sortBy.toLowerCase())).stream().map(FilmMapper::maptoFilmDTO).toList();
    }

    public FilmDTO findFilmById(long filmId) {
        log.trace("Произведен вызов фильма из базы данных с ID: {}", filmId);
        return filmDb.findById(filmId).map(FilmMapper::maptoFilmDTO).orElseThrow(() -> new NotFoundException("Не удалось найти фильм"));
    }


    public FilmDTO createFilm(NewFilmRequest film) {
        Film createdFilm = FilmMapper.mapToFilm(film);
        createdFilm = filmDb.create(createdFilm);
        log.info("Создан новый фильм с ID: {}.{}", createdFilm.getId(), createdFilm.getName());
        return FilmMapper.maptoFilmDTO(createdFilm);
    }

    public FilmDTO updateFilm(UpdateFilmRequest film) {
        Film film1 = filmDb.findById(film.getId()).map(film2 -> FilmMapper.updateFieldsFilms(film2, film))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        film1 = filmDb.update(film1);
        log.info("Фильм с ID: {}.{} был обновлен", film.getId(), film.getName());
        return FilmMapper.maptoFilmDTO(film1);
    }

    public boolean deleteFilm(long filmId) {
        Film film = searchFilm(filmId);
        log.info("Фильм с ID: {}.{} был удален", film.getId(), film.getName());
        return filmDb.delete(film.getId());
    }

    public List<FilmDTO> getPopularFilm(Integer count) {
        log.trace("Был произведен вывод популярных фильмов");
        return filmDb.popularFilms(count).stream().map(FilmMapper::maptoFilmDTO).toList();
    }


    public FilmDTO setLike(Long idFilm, Long idUser) {
        checkUser(idUser);
        Film film = searchFilm(idFilm);
        if (film.getLikes().contains(idUser)) {
            throw new ValidationException("Пользователь с id = " + idUser + "уже поставил лайк");
        }
        if (filmDb.addLike(idFilm, idUser)) {
            film.getLikes().add(idUser);
        }
        log.trace("Пользователь с id={} поставил лайк фильму {}", idUser, film.getName());
        return FilmMapper.maptoFilmDTO(film);
    }

    public FilmDTO removeLike(Long idFilm, Long idUser) {
        checkUser(idUser);
        Film film = searchFilm(idFilm);
        if (filmDb.removeLike(idFilm, idUser)) {
            film.getLikes().remove(idUser);
        }
        log.trace("Пользователь с id={} убрал лайк фильму {}", idUser, film.getName());
        return FilmMapper.maptoFilmDTO(film);
    }

    public List<FilmDTO> findCommonFilms(Long userId, Long friendId) {
        return filmDb.findCommonFilms(userId, friendId).stream().map(FilmMapper::maptoFilmDTO).toList();
    }


    private Film searchFilm(Long idFilm) {
        return filmDb.findById(idFilm).orElseThrow(() ->
                new NotFoundException("Не удалось найти фильм"));
    }

    private void checkUser(long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + userId + " не найден");
        }
    }

}
