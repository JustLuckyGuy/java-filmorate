package ru.yandex.practicum.filmorate.dto.update_request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.annotations.AfterMinDay;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class UpdateFilmRequest {
    @NotNull
    @Positive
    private Long id;
    private String name;
    @Size(max = 200, message = "Превышен лимит символов")
    private String description;
    @AfterMinDay
    private LocalDate releaseDate;
    @Min(1)
    private Long duration;
    private final Set<Genre> genres = new HashSet<>();
    private final Set<Director> directors = new HashSet<>();
    private MPA mpa;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null;
    }

    public boolean hasMpa() {
        return mpa != null;
    }

    public boolean hasGenres() {
        return !genres.isEmpty();
    }

    public boolean hasDirectors() {
        return !directors.isEmpty();
    }

}
