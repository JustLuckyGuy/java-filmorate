package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.new_request.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.update_request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<FilmDTO> allFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public FilmDTO findFilm(@PathVariable @NotNull @Positive long filmId) {
        return filmService.findFilmById(filmId);
    }

    @GetMapping("/popular")
    public List<FilmDTO> getPopular(@RequestParam(defaultValue = "10") @Positive int count) {
        return filmService.getPopularFilm(count);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDTO> getPopularDirectorFilms(@PathVariable @NotNull @Positive long directorId,
                                                 @RequestParam(required = false) String sortBy) {
        return filmService.getFilmsDirector(directorId, sortBy);
    }

    @PostMapping
    public FilmDTO create(@RequestBody @Validated NewFilmRequest film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public FilmDTO update(@RequestBody @Validated UpdateFilmRequest film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{filmId}")
    public boolean delete(@PathVariable @NotNull @Positive long filmId) {
        return filmService.deleteFilm(filmId);
    }


    @PutMapping("/{filmId}/like/{userId}")
    public FilmDTO setLike(@PathVariable @NotNull @Positive Long filmId,
                           @PathVariable @NotNull @Positive Long userId) {

        return filmService.setLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public FilmDTO removeLike(@PathVariable @NotNull @Positive Long filmId,
                              @PathVariable @NotNull @Positive Long userId) {
        return filmService.removeLike(filmId, userId);
    }


}