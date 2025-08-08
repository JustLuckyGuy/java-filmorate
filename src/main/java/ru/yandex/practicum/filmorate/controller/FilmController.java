package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> allFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {

        return filmService.getPopularFilm(count);
    }

    @PostMapping
    public Film create(@RequestBody @Validated Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody @Validated Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film setLike(@PathVariable Long filmId,
                        @PathVariable Long userId) {
        if (filmId == null || userId == null) {
            log.error("Поставить лайк: Не введен id одного из полей");
            throw new ParameterNotValidException("Проверьте правильность ввода id фильма или id пользователя");
        }
        return filmService.setLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable Long filmId,
                           @PathVariable Long userId) {
        if (filmId == null || userId == null) {
            log.error("Удалить лайк: Не введен id одного из полей");
            throw new ParameterNotValidException("Проверьте правильность ввода id фильма или id пользователя");
        }
        return filmService.removeLike(filmId, userId);
    }


}