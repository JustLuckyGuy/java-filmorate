package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MPAController {
    private final MPAService mpaService;

    @GetMapping
    public Collection<MPA> allGenres() {
        return mpaService.getAllMPA();
    }

    @GetMapping("/{mpaId}")
    public MPA getGenre(@PathVariable @NotNull @Positive long mpaId) {
        return mpaService.getMpaById(mpaId);
    }

    @PostMapping
    public MPA addGenre(@RequestBody MPA mpa) {
        return mpaService.createMpa(mpa);
    }

    @PutMapping("/{mpaId}")
    public MPA updateGenre(@PathVariable @NotNull @Positive long mpaId, @RequestBody MPA newRating) {
        return mpaService.update(mpaId, newRating);
    }

    @DeleteMapping("/{mpaId}")
    public boolean deleteGenre(@PathVariable @NotNull @Positive long mpaId) {
        return mpaService.delete(mpaId);
    }
}
