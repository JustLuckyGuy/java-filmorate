package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("FilmDb")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;

    private static final String FIND_ALL_FILMS = "SELECT * FROM film";
    private static final String FIND_FILM_BY_ID = "SELECT * FROM film WHERE film_id = ?";
    private static final String INSERT_FILM = "INSERT INTO film(name, description, release_date, duration, mpa_id) VALUES (?,?,?,?,?)";
    private static final String UPDATE_FILM = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String DELETE_FILM = "DELETE FROM film WHERE film_id = ?";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Film> mapper, GenreRepository genreRepository, MpaRepository mpaRepository) {
        super(jdbcTemplate, mapper);
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
    }

    public List<Film> allFilms(){
        List<Film> films = findMany(FIND_ALL_FILMS);
        for(Film film : films){
            completeAssemblyFilm(film);
        }
        return films;
    }

    public Optional<Film> findById(long filmId){
        Optional<Film> film = findOne(FIND_FILM_BY_ID, filmId);
        film.ifPresent(this::completeAssemblyFilm);
        return film;
    }


    public Film create(Film film){
        Long id = insert(INSERT_FILM,"film_id", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        film.setId(id);
        if(!film.getGenres().isEmpty()){
            for(Genre genre : film.getGenres()){
                genreRepository.addRelationship(film.getId(), genre.getId());
            }
        }
        return film;
    }

    public Film update(Film film){
        update(UPDATE_FILM, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        if(!film.getGenres().isEmpty()){
            genreRepository.deleteRelationship(film.getId());
            for(Genre genre : film.getGenres()){
                genreRepository.addRelationship(film.getId(), genre.getId());
            }
        }
        return film;
    }

    public boolean delete(long filmId){
        return delete(DELETE_FILM, filmId);
    }

    private void completeAssemblyFilm(Film film){
        film.getGenres().clear();
        List<Genre> genres = genreRepository.findByIdFilm(film.getId());
        film.getGenres().addAll(genres);
        Optional<MPA> mpa = mpaRepository.findByIdFilm(film.getId());
        mpa.ifPresent(film::setMpa);
    }




}
