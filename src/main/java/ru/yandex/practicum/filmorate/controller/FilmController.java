package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public List<Film> getPopular(@RequestParam(defaultValue = "10") @Positive int count) {
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
    public Film setLike(@PathVariable @NotNull @Positive Long filmId,
                        @PathVariable @NotNull @Positive Long userId) {

        return filmService.setLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable @NotNull @Positive Long filmId,
                           @PathVariable @NotNull @Positive Long userId) {
        return filmService.removeLike(filmId, userId);
    }


}