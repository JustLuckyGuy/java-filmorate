package ru.yandex.practicum.filmorate.dto.new_request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.annotations.AfterMinDay;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class NewFilmRequest {
    @NotBlank(message = "Поле должно быть заполнено")
    @NotNull(message = "Заполните поле")
    private String name;
    @Size(max = 200, message = "Превышен лимит символов")
    private String description;
    @AfterMinDay
    private LocalDate releaseDate;
    @Min(1)
    private Long duration;
    private final Set<Genre> genres = new HashSet<>();
    private final MPA mpa;
}
