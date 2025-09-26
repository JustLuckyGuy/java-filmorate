package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Repository
public class DirectorRepository extends BaseRepository<Director> {
    private static final String FIND_ALL_DIRECTORS = "SELECT * FROM directors";
    private static final String FIND_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE director_id = ?";
    private static final String FIND_ALL_DIRECTOR_ID_FILM = "select * from directors " +
            "WHERE director_id IN (SELECT director_id from director_film WHERE film_id = ?)";
    private static final String INSERT_DIRECTOR = "INSERT INTO directors (name) VALUES (?)";
    private static final String UPDATE_DIRECTOR = "UPDATE directors SET name = ? WHERE director_id = ? ";
    private static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE director_id = ? ";
    private static final String INSERT_RELATIONSHIP = "INSERT INTO director_film(film_id, director_id) VALUES (?,?)";
    private static final String DELETE_RELATIONSHIP = "DELETE FROM director_film WHERE film_id = ?";

    public DirectorRepository(JdbcTemplate jdbcTemplate, RowMapper<Director> mapper) {
        super(jdbcTemplate, mapper);
    }

    public List<Director> findAllDirectors() {
        return findMany(FIND_ALL_DIRECTORS);
    }

    public List<Director> findByIdFilm(long filmId) {
        return findMany(FIND_ALL_DIRECTOR_ID_FILM, filmId);
    }

    public Optional<Director> findByIdDirector(long directorId) {
        return findOne(FIND_DIRECTOR_BY_ID, directorId);
    }

    public void addRelationship(long filmId, long directorId) {
        insert(INSERT_RELATIONSHIP, "film_id", filmId, directorId);
    }

    public void deleteRelationship(long filmId) {
        delete(DELETE_RELATIONSHIP, filmId);
    }

    public Director save(Director director) {
        long id = insert(INSERT_DIRECTOR, "director_id", director.getName());
        director.setId(id);
        return director;
    }

    public Director update(Director director) {
        update(UPDATE_DIRECTOR, director.getName(), director.getId());
        return director;
    }

    public boolean delete(long directorId) {
        return delete(DELETE_DIRECTOR, directorId);
    }
}
