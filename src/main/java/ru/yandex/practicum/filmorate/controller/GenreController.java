package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> allGenres(){
        return genreService.getAllGenres();
    }

    @GetMapping("/{genreId}")
    public Genre getGenre(@PathVariable @NotNull @Positive long genreId){
        return genreService.getGenreById(genreId);
    }

    @PostMapping
    public Genre addGenre(@RequestBody Genre genre){
        return genreService.createGenre(genre);
    }

    @PutMapping("/{genreId}")
    public Genre updateGenre(@PathVariable @NotNull @Positive long genreId, @RequestBody Genre newName){
        return genreService.update(genreId, newName);
    }

    @DeleteMapping("/{genreId}")
    public boolean deleteGenre(@PathVariable @NotNull @Positive long genreId){
        return genreService.delete(genreId);
    }

    @DeleteMapping("/film/{filmId}")
    public boolean deleteR(@PathVariable @NotNull @Positive long filmId){
        return genreService.deleteR(filmId);
    }
}
