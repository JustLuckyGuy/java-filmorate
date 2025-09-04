package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final JdbcTemplate jdbcTemplate;
    private final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbcTemplate.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbcTemplate.query(query, mapper, params);
    }

    protected boolean delete(String query, long id) {
        int rowsDeleted = jdbcTemplate.update(query, id);
        return rowsDeleted > 0;
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbcTemplate.update(query, params);
        if (rowsUpdated == 0) {
            throw new NotFoundException("Не удалось обновить данные");
        }
    }

    protected long insert(String query, String nameColumn, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            // Явно указываем имя столбца с автоинкрементным ID
            PreparedStatement ps = connection.prepareStatement(query, new String[]{nameColumn});
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        // Получаем ID через Map, а не напрямую
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.containsKey(nameColumn)) {
            return ((Number) keys.get(nameColumn)).longValue();
        } else {
            throw new NotFoundException("Не удалось сохранить данные");
        }
    }
}
