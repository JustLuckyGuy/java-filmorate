package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<Director> getDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/{directorId}")
    public Director findFilm(@PathVariable @NotNull @Positive long directorId) {
        return directorService.getDirectorById(directorId);
    }

    @PostMapping
    public Director create(@RequestBody @Validated Director directorRequest) {
        return directorService.createDirector(directorRequest);
    }

    @PutMapping
    public Director update(@RequestBody @Validated Director directorRequest) {
        return directorService.update(directorRequest);
    }

    @DeleteMapping("/{directorId}")
    public boolean delete(@PathVariable @NotNull @Positive long directorId) {
        return directorService.delete(directorId);
    }

}
