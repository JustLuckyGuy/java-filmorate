package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage inMemoryFilmStorage;
    private final UserStorage inMemoryUserStorage;
    private final Comparator<Film> comparator = Comparator.comparingLong((Film film) -> film.getLikes().size()).reversed();

    public Film setLike(Long idFilm, Long idUser) {
        Film film = searchFilm(idFilm, idUser);
        if (film.getLikes().contains(idUser)) {
            throw new ValidationException("Пользователь с id = " + idUser + "уже поставил лайк");
        }
        film.getLikes().add(idUser);
        log.trace("Пользователь с id={} поставил лайк фильму {}", idUser, film.getName());
        return film;
    }

    public Film removeLike(Long idFilm, Long idUser) {
        Film film = searchFilm(idFilm, idUser);
        film.getLikes().remove(idUser);
        log.trace("Пользователь с id={} убрал лайк фильму {}", idUser, film.getName());
        return film;
    }

    public List<Film> getPopularFilm(Integer count) {
        log.trace("Был произведен вывод популярных фильмов");
        return inMemoryFilmStorage.allFilms().stream()
                .sorted(comparator)
                .limit(count)
                .toList();
    }

    private Film searchFilm(Long idFilm, Long idUser) {
        Optional<User> user = inMemoryUserStorage.findById(idUser);
        Optional<Film> film = inMemoryFilmStorage.findById(idFilm);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + idUser + " не найден");

        }

        if (film.isEmpty()) {
            throw new NotFoundException("Не удалось найти фильм");

        }
        return film.get();
    }

}
