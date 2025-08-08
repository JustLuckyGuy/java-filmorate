package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage inMemoryFilmStorage;
    private final UserStorage inMemoryUserStorage;
    private final Comparator<Film> comparator = Comparator.comparingLong((Film film) -> film.getLikes().size()).reversed();

    public Collection<Film> getAllFilms() {
        return inMemoryFilmStorage.allFilms();
    }

    public Film createFilm(Film film) {
        return inMemoryFilmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return inMemoryFilmStorage.update(film);
    }

    public Film setLike(Long idFilm, Long idUser) {
        if (idFilm <= 0 || idUser <= 0) {
            log.error("Поставить лайк: id не может быть отрицательным");
            throw new ParameterNotValidException("Не верно указан id пользователя или id фильма");
        }
        Film film = searchFilm(idFilm, idUser);
        if (film.getLikes().contains(idUser)) {
            throw new ValidationException("Пользователь с id = " + idUser + "уже поставил лайк");
        }
        film.getLikes().add(idUser);
        log.trace("Пользователь с id={} поставил лайк фильму {}", idUser, film.getName());
        return film;
    }

    public Film removeLike(Long idFilm, Long idUser) {
        if (idFilm <= 0 || idUser <= 0) {
            log.error("Удалить лайк: id не может быть отрицательным");
            throw new ParameterNotValidException("Не верно указан id пользователя или id фильма");
        }
        Film film = searchFilm(idFilm, idUser);
        film.getLikes().remove(idUser);
        log.trace("Пользователь с id={} убрал лайк фильму {}", idUser, film.getName());
        return film;
    }

    public List<Film> getPopularFilm(Integer count) {
        if (count <= 0) {
            log.error("count не должно быть отрицательным");
            throw new ParameterNotValidException("Ошибка с вводом числа count");
        }
        log.trace("Был произведен вывод популярных фильмов");
        return inMemoryFilmStorage.allFilms().stream()
                .sorted(comparator)
                .limit(count)
                .toList();
    }

    private Film searchFilm(Long idFilm, Long idUser) {
        User user = inMemoryUserStorage.findById(idUser).orElseThrow(() ->
                new NotFoundException("Пользователь c id = " + idUser + " не найден"));

        return inMemoryFilmStorage.findById(idFilm).orElseThrow(() ->
                new NotFoundException("Не удалось найти фильм"));
    }

}
