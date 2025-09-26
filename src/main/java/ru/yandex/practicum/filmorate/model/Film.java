package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private final Set<Long> likes = new HashSet<>();
    private final Set<Genre> genres = new HashSet<>();
    private final Set<Director> directors = new HashSet<>();
    private MPA mpa;
}
