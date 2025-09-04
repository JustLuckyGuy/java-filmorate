package ru.yandex.practicum.filmorate.storage.film;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private long id = 1;

    private final Map<Long, Film> filmCollection = new HashMap<>();


    @Override
    public Collection<Film> allFilms() {
        log.info("Список всех фильмов:\n");
        return filmCollection.values();
    }

    @Override
    public Optional<Film> findById(long id) {
        return Optional.ofNullable(filmCollection.get(id));
    }

    @Override
    public boolean delete(long id) {
        return false;
    }

    @Override
    public Film create(Film film) {
        film.setId(id);
        id++;
        filmCollection.put(film.getId(), film);
        log.info("Фильм №{}. {} - создан", film.getId(), film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() == null) {
            log.error("Ошибка обновления фильма: ID пользователя не было указано");
            throw new ValidationException("Id должно быть указано");
        }
        if (filmCollection.containsKey(film.getId())) {
            Film oldFilm = filmCollection.get(film.getId());
            oldFilm.setName(film.getName());
            if (film.getDescription() != null && !film.getDescription().isBlank())
                oldFilm.setDescription(film.getDescription());
            if (film.getReleaseDate() != null) oldFilm.setReleaseDate(film.getReleaseDate());
            if (film.getDuration() != null) oldFilm.setDuration(film.getDuration());
            log.info("Фильм №{}. {} - был обновлен", oldFilm.getId(), oldFilm.getName());
            return oldFilm;
        }
        log.error("Ошибка обновления фильма: не найден фильм с ID {}", film.getId());
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }
}
