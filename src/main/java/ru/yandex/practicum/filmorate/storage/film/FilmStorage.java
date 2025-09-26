package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.SortOrder;

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

    boolean removeLike(Long filmId, long userId);

    List<Film> popularFilms(int count, Integer year, Long id);

    List<Film> allFilmsOfDirector(Long id, SortOrder sort);

    List<Film> findCommonFilms(Long userId, Long friendId);

    List<Film> searchByTitle(String query);

    List<Film> searchByDirector(String query);

    List<Film> searchByTitleAndByDirector(String query);
}
