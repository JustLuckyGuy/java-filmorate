package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.new_request.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.update_request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    public static FilmDTO maptoFilmDTO(Film film) {
        FilmDTO filmDTO = new FilmDTO();
        filmDTO.setId(film.getId());
        filmDTO.setName(film.getName());
        filmDTO.setDescription(film.getDescription());
        filmDTO.setReleaseDate(film.getReleaseDate());
        filmDTO.setDuration(film.getDuration());
        filmDTO.getGenres().addAll(film.getGenres());
        filmDTO.getDirectors().addAll(film.getDirectors());
        if (film.getMpa() != null) {
            filmDTO.setMpa(film.getMpa());
        }

        return filmDTO;
    }

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = Film.builder()
                .name(request.getName())
                .description(request.getDescription())
                .releaseDate(request.getReleaseDate())
                .duration(request.getDuration())
                .build();
        if (request.getMpa() != null) {
            film.setMpa(request.getMpa());
        }
        film.getGenres().addAll(request.getGenres());
        film.getDirectors().addAll(request.getDirectors());
        return film;
    }

    public static Film updateFieldsFilms(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasMpa()) {
            film.setMpa(request.getMpa());
        }
        if (request.hasGenres()) {
            film.getGenres().addAll(request.getGenres());
        }
        if (request.hasDirectors()) {
            film.getDirectors().addAll(request.getDirectors());
        }

        return film;
    }
}
