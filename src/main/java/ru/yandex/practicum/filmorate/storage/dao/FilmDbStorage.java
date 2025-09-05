package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("filmdb")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;

    private static final String FIND_ALL_FILMS = "SELECT * FROM film";
    private static final String FIND_FILM_BY_ID = "SELECT * FROM film WHERE film_id = ?";
    private static final String FIND_POPULAR_FILMS = "SELECT f.* FROM film AS f WHERE f.film_id IN (" +
            "SELECT fk.film_id FROM likes AS fk " +
            "GROUP BY fk.film_id ORDER BY COUNT(fk.user_id) DESC " +
            "LIMIT ?) " +
            "ORDER BY (SELECT COUNT(*) FROM likes AS fl " +
            "WHERE fl.film_id = f.film_id) DESC;";
    private static final String FIND_ALL_LIKES = "SELECT user_id FROM likes WHERE film_id =?";
    private static final String INSERT_FILM = "INSERT INTO film(name, description, release_date, duration, mpa_id) VALUES (?,?,?,?,?)";
    private static final String UPDATE_FILM = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String DELETE_FILM = "DELETE FROM film WHERE film_id = ?";
    private static final String INSERT_LIKE = "INSERT INTO likes(film_id, user_id) VALUES(?,?)";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE user_id = ?";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Film> mapper, GenreRepository genreRepository, MpaRepository mpaRepository) {
        super(jdbcTemplate, mapper);
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
    }

    public List<Film> allFilms() {
        List<Film> films = findMany(FIND_ALL_FILMS);
        for (Film film : films) {
            completeAssemblyFilm(film);
        }
        return films;
    }

    public Optional<Film> findById(long filmId) {
        Optional<Film> film = findOne(FIND_FILM_BY_ID, filmId);
        film.ifPresent(this::completeAssemblyFilm);
        return film;
    }


    public Film create(Film film) {
        if (mpaRepository.findByIdMPA(film.getMpa().getId()).isEmpty()) {
            throw new NotFoundException("Такого рейтинга не существует");
        }
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (genreRepository.findByIdGenre(genre.getId()).isEmpty()) {
                    throw new NotFoundException("Такого жанра не существует");
                }
            }
        }
        Long mpaId = (film.getMpa() != null) ? film.getMpa().getId() : null;

        Long id = insert(INSERT_FILM, "film_id", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), mpaId);
        film.setId(id);
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreRepository.addRelationship(film.getId(), genre.getId());
            }
        }
        return film;
    }

    public Film update(Film film) {
        update(UPDATE_FILM, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        if (!film.getGenres().isEmpty()) {
            genreRepository.deleteRelationship(film.getId());
            for (Genre genre : film.getGenres()) {
                genreRepository.addRelationship(film.getId(), genre.getId());
            }
        }
        return film;
    }

    public boolean delete(long filmId) {
        return delete(DELETE_FILM, filmId);
    }

    public boolean addLike(long filmId, long userId) {
        try {
            int row = jdbcTemplate.update(INSERT_LIKE, filmId, userId);
            return row > 0;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    public boolean removeLike(long userId) {
        int row = jdbcTemplate.update(DELETE_LIKE, userId);
        return row > 0;
    }

    public List<Film> popularFilms(int count) {
        List<Film> films = findMany(FIND_POPULAR_FILMS, count);
        for (Film film : films) {
            completeAssemblyFilm(film);
        }
        return films;
    }

    private void completeAssemblyFilm(Film film) {
        film.getGenres().clear();
        List<Genre> genres = genreRepository.findByIdFilm(film.getId());
        film.getGenres().addAll(genres);
        film.getLikes().clear();
        List<Long> likes = jdbcTemplate.queryForList(FIND_ALL_LIKES, Long.class, film.getId());
        film.getLikes().addAll(likes);
        Optional<MPA> mpa = mpaRepository.findByIdFilm(film.getId());
        mpa.ifPresent(film::setMpa);
    }
}
