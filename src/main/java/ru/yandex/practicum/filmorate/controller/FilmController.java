package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> allFilms() {
        return filmStorage.allFilms();
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilm(count);
    }

    @PostMapping
    public Film create(@RequestBody @Validated Film film) {
        return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@RequestBody @Validated Film film) {
        return filmStorage.update(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film setLike(@PathVariable Long filmId,
                        @PathVariable Long userId) {
        if (filmId == null || userId == null) {
            throw new ParameterNotValidException("Проверьте правильность ввода id фильма или id пользователя");
        }
        return filmService.setLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable Long filmId,
                           @PathVariable Long userId) {
        if (filmId == null || userId == null) {
            throw new ParameterNotValidException("Проверьте правильность ввода id фильма или id пользователя");
        }
        return filmService.removeLike(filmId, userId);
    }


}