package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.new_request.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.update_request.UpdateFilmRequest;
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

    @GetMapping("/{filmId}")
    public FilmDTO findFilm(@PathVariable @NotNull @Positive long filmId){
        return filmService.findFilmById(filmId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") @Positive int count) {
        return filmService.getPopularFilm(count);
    }

    @PostMapping
    public FilmDTO create(@RequestBody @Validated NewFilmRequest film) {
        return filmService.createFilm(film);
    }

    @PutMapping("/{filmId}")
    public FilmDTO update(@PathVariable @NotNull @Positive long filmId, @RequestBody @Validated UpdateFilmRequest film) {
        return filmService.updateFilm(filmId, film);
    }

    @DeleteMapping("/{filmId}")
    public String delete(@PathVariable @NotNull @Positive long filmId){
        return filmService.deleteFilm(filmId) ? "Фильм с id " + filmId + " успешно удален" : "Не удалось удалить фильм";
    }



//    @PutMapping("/{filmId}/like/{userId}")
//    public Film setLike(@PathVariable @NotNull @Positive Long filmId,
//                        @PathVariable @NotNull @Positive Long userId) {
//
//        return filmService.setLike(filmId, userId);
//    }
//
//    @DeleteMapping("/{filmId}/like/{userId}")
//    public Film removeLike(@PathVariable @NotNull @Positive Long filmId,
//                           @PathVariable @NotNull @Positive Long userId) {
//        return filmService.removeLike(filmId, userId);
//    }


}