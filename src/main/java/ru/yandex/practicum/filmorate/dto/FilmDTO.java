package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private final Set<Genre> genres = new HashSet<>();
    private MPA mpa;
}
