package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.annotations.AfterMinDay;
import ru.yandex.practicum.filmorate.model.annotations.PositiveDuration;
import ru.yandex.practicum.filmorate.serializations.DurationDeserializer;
import ru.yandex.practicum.filmorate.serializations.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;

@Builder
@Data
public class Film {
    private Long id;
    @NotBlank(message = "Поле должно быть заполнено")
    private String name;
    @Size(max = 200, message = "Превышен лимит символов")
    private String description;
    @AfterMinDay
    private LocalDate releaseDate;
    @JsonDeserialize(using = DurationDeserializer.class)
    @JsonSerialize(using = DurationSerializer.class)
    @PositiveDuration
    private Duration duration;
}
