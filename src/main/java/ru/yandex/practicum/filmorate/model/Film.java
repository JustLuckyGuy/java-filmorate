package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.annotations.AfterMinDay;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
public class Film {
    @Min(1)
    private Long id;
    @NotBlank(message = "Поле должно быть заполнено")
    private String name;
    @Size(max = 200, message = "Превышен лимит символов")
    private String description;
    @AfterMinDay
    private LocalDate releaseDate;
    @Min(1)
    private Long duration;
    private final Set<Long> likes = new HashSet<>();
}
