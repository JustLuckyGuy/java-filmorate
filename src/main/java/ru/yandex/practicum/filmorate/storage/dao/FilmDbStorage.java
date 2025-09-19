package ru.yandex.practicum.filmorate.storage.dao;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("filmdb")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final DirectorRepository directorRepository;

    private static final String FIND_ALL_FILMS = "SELECT * FROM film";
    private static final String FIND_FILM_BY_ID = "SELECT * FROM film WHERE film_id = ?";
    private static final String FIND_POPULAR_FILMS = "SELECT f.* FROM film AS f WHERE f.film_id IN (" +
            "SELECT fk.film_id FROM likes AS fk " +
            "GROUP BY fk.film_id ORDER BY COUNT(fk.user_id) DESC " +
            "LIMIT ?) " +
            "ORDER BY (SELECT COUNT(*) FROM likes AS fl " +
            "WHERE fl.film_id = f.film_id) DESC;";
    private static final String FIND_POPULAR_FILMS_WITH_GENRE = "SELECT f.* FROM film f " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "INNER JOIN genre_film gf ON f.film_id = gf.film_id AND gf.genre_id = ? " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";
    private static final String FIND_POPULAR_FILMS_WITH_YEAR = "SELECT f.* FROM film f " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "WHERE YEAR(f.release_date) = ? " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";
    private static final String FIND_POPULAR_FILMS_WITH_GENRE_AND_YEAR = "SELECT f.* FROM film f " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "INNER JOIN genre_film gf ON f.film_id = gf.film_id AND gf.genre_id = ? " +
            "WHERE YEAR(f.release_date) = ? " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";
    private static final String FIND_DIRECTOR_FILMS = "SELECT * FROM film WHERE film_id IN (" +
            "SELECT film_id FROM director_film WHERE director_id = ?) " +
            "ORDER BY film_id";
    private static final String FIND_DIRECTOR_FILMS_YEAR = "SELECT * FROM film WHERE film_id IN (" +
            "SELECT film_id FROM director_film WHERE director_id = ?) " +
            "ORDER BY film.release_date";
    private static final String FIND_DIRECTOR_FILMS_LIKES = "SELECT * FROM film WHERE film_id IN (" +
            "SELECT film_id FROM director_film WHERE director_id = ?) " +
            "ORDER BY (SELECT COUNT(*) FROM likes as l WHERE l.film_id = film.film_id) DESC";
    private static final String INSERT_FILM = "INSERT INTO film(name, description, release_date, duration, mpa_id) VALUES (?,?,?,?,?)";
    private static final String UPDATE_FILM = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String DELETE_FILM = "DELETE FROM film WHERE film_id = ?";
    private static final String INSERT_LIKE = "INSERT INTO likes(film_id, user_id) VALUES(?,?)";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE user_id = ?";
    private static final String SEARCH_BY_TITLE = "SELECT f.*, m.mpa_id as mpa_id, m.code as mpa_name " +
            "FROM film f LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE LOWER(f.name) LIKE ?";
    private static final String SEARCH_BY_DIRECTOR = "SELECT f.*, m.mpa_id as mpa_id, m.code as mpa_name FROM film f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id LEFT JOIN director_film fd ON f.film_id = fd.film_id " +
            "LEFT JOIN directors d ON fd.director_id = d.director_id WHERE LOWER(d.name) LIKE ?";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Film> mapper, GenreRepository genreRepository, MpaRepository mpaRepository, DirectorRepository directorRepository) {
        super(jdbcTemplate, mapper);
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
        this.directorRepository = directorRepository;
    }

    public List<Film> allFilms() {
        List<Film> films = findMany(FIND_ALL_FILMS);
        for (Film film : films) {
            completeAssemblyFilm(film);
        }
        return films;
    }

    public List<Film> allFilmsOfDirector(Long directorId, SortOrder sortBy) {
        List<Film> films;
        switch (sortBy) {
            case SORT_BY_LIKES -> films = findMany(FIND_DIRECTOR_FILMS_LIKES, directorId);
            case SORT_BY_YEAR -> films = findMany(FIND_DIRECTOR_FILMS_YEAR, directorId);
            default -> films = findMany(FIND_DIRECTOR_FILMS, directorId);
        }

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
        mpaRepository.findByIdMPA(film.getMpa().getId()).orElseThrow(() -> new NotFoundException("Такого рейтинга не существует"));
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreRepository.findByIdGenre(genre.getId()).orElseThrow(() -> new NotFoundException("Такого жанра не существует"));
            }
        }
        if (!film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                directorRepository.findByIdDirector(director.getId()).orElseThrow(() -> new NotFoundException("Данный режиссер не найден"));
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
        if (!film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                directorRepository.addRelationship(film.getId(), director.getId());
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
        if (!film.getDirectors().isEmpty()) {
            directorRepository.deleteRelationship(film.getId());
            for (Director director : film.getDirectors()) {
                directorRepository.addRelationship(film.getId(), director.getId());
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
            log.warn("Пользователь c ID: {} уже поставил лайк фильму ID: {}", userId, filmId);
            return false;
        }
    }

    public boolean removeLike(long userId) {
        int row = jdbcTemplate.update(DELETE_LIKE, userId);
        return row > 0;
    }

    public List<Film> popularFilms(int count, Integer year, Long genreId) {
        List<Film> films = checkFilters(count, year, genreId);
        for (Film film : films) {
            completeAssemblyFilm(film);
        }
        return films;
    }


    private void completeAssemblyFilm(Film film) {
        log.trace("Поиск всех жанров и рейтинга у фильма с ID: {}.{}", film.getId(), film.getName());
        film.getGenres().clear();
        List<Genre> genres = genreRepository.findByIdFilm(film.getId());
        film.getGenres().addAll(genres);
        Optional<MPA> mpa = mpaRepository.findByIdFilm(film.getId());
        mpa.ifPresent(film::setMpa);
        film.getDirectors().clear();
        List<Director> directors = directorRepository.findByIdFilm(film.getId());
        film.getDirectors().addAll(directors);
    }

    private List<Film> checkFilters(int count, Integer year, Long genreId) {
        if (genreId != null) {
            genreRepository.findByIdGenre(genreId).orElseThrow(() -> new NotFoundException("Жанр не найден"));
        }
        if (year != null && genreId != null) {
            return findMany(FIND_POPULAR_FILMS_WITH_GENRE_AND_YEAR, genreId, year, count);
        } else if (genreId != null) {
            return findMany(FIND_POPULAR_FILMS_WITH_GENRE, genreId, count);
        } else if (year != null) {
            return findMany(FIND_POPULAR_FILMS_WITH_YEAR, year, count);
        } else {
            return findMany(FIND_POPULAR_FILMS, count);
        }
    }

    @Override
    public List<Film> searchByTitle(String query) {
        List<Film> films = findMany(SEARCH_BY_TITLE, "%" + query + "%");
        films.forEach(this::completeAssemblyFilm);
        return films;
    }

    @Override
    public List<Film> searchByDirector(String query) {
        List<Film> films = findMany(SEARCH_BY_DIRECTOR, "%" + query + "%");
        films.forEach(this::completeAssemblyFilm);
        return films;
    }

}
