package ru.yandex.practicum.filmorate.storage.dao;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<MPA> {
    private static final String FIND_ALL_RATINGS = "SELECT * FROM mpa";
    private static final String FIND_BY_ID_FILM = "SELECT * FROM mpa WHERE mpa_id = (SELECT mpa_id FROM film WHERE film_id = ?)";
    private static final String FIND_MPA_BY_ID = "SELECT * FROM mpa WHERE mpa_id = ?";
    private static final String INSERT_MPA = "INSERT INTO mpa(code) VALUES (?)";
    private static final String UPDATE_MPA = "UPDATE mpa SET code = ? WHERE mpa_id = ?";
    private static final String DELETE_MPA = "DELETE FROM mpa WHERE mpa_id = ?";

    public MpaRepository(JdbcTemplate jdbcTemplate, RowMapper<MPA> mapper) {
        super(jdbcTemplate, mapper);
    }

    public List<MPA> findAllMPA() {
        return findMany(FIND_ALL_RATINGS);
    }

    public Optional<MPA> findByIdFilm(long filmId) {
        return findOne(FIND_BY_ID_FILM, filmId);
    }

    public Optional<MPA> findByIdMPA(long mpaId) {
        return findOne(FIND_MPA_BY_ID, mpaId);
    }

    public MPA save(MPA mpa) {
        long id = insert(INSERT_MPA, "mpa_id", mpa.getName());
        mpa.setId(id);
        return mpa;
    }

    public MPA update(MPA mpa) {
        update(UPDATE_MPA, mpa.getName(), mpa.getId());
        return mpa;
    }

    public boolean delete(long mpaId) {
        return delete(DELETE_MPA, mpaId);
    }
}
