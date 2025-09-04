package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String FIND_ALL_GENRES = "SELECT * FROM genre";
    private static final String FIND_ALL_GENRES_ID_FILM = "select * from genre " +
            "WHERE genre_id IN (SELECT genre_id from genre_film WHERE film_id = ?)";
    private static final String FIND_GENRE_BY_ID = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String INSERT_GENRE = "INSERT INTO genre (name) VALUES (?)";
    private static final String UPDATE_GENRE = "UPDATE genre SET name = ? WHERE genre_id = ? ";
    private static final String DELETE_GENRE = "DELETE FROM genre WHERE genre_id = ? ";
    private static final String INSERT_RELATIONSHIP = "INSERT INTO genre_film(film_id, genre_id) VALUES (?,?)";
    private static final String DELETE_RELATIONSHIP = "DELETE FROM genre_film WHERE film_id = ?";

    public GenreRepository(JdbcTemplate jdbcTemplate, RowMapper<Genre> mapper) {
        super(jdbcTemplate, mapper);
    }

    public List<Genre> findAllGenre(){
        return findMany(FIND_ALL_GENRES);
    }

    public List<Genre> findByIdFilm(long filmId){
        return findMany(FIND_ALL_GENRES_ID_FILM, filmId);
    }

    public Optional<Genre> findByIdGenre(long genreId){
        return findOne(FIND_GENRE_BY_ID, genreId);
    }

    public void addRelationship(long filmId, long genreId){
        insert(INSERT_RELATIONSHIP,"film_id", filmId, genreId);
    }

    public boolean deleteRelationship(long filmId){
        delete(DELETE_RELATIONSHIP, filmId);
        return false;
    }

    public Genre save(Genre genre){
        long id = insert(INSERT_GENRE,"genre_id", genre.getName());
        genre.setId(id);
        return genre;
    }

    public Genre update(Genre genre){
        update(UPDATE_GENRE, genre.getName(), genre.getId());
        return genre;
    }

    public boolean delete(long genreId){
        return delete(DELETE_GENRE, genreId);
    }
}
