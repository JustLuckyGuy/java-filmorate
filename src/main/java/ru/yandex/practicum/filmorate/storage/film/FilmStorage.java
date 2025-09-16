package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface FilmStorage {
    Collection<Film> allFilms();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> findById(long id);

    boolean delete(long id);

    boolean addLike(long filmId, long userId);

    boolean removeLike(long userId);

    List<Film> popularFilms(int count);

    List<Film> allFilmsOfDirector(Long id, String sort);
}
