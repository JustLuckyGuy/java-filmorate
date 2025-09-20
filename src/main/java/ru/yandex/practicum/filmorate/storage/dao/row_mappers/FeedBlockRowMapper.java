package ru.yandex.practicum.filmorate.storage.dao.row_mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FeedBlock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class FeedBlockRowMapper implements RowMapper<FeedBlock> {

    @Override
    public FeedBlock mapRow(ResultSet rs, int rowNum) throws SQLException {
        FeedBlock block = new FeedBlock();
        block.setEventId(rs.getLong("event_id"));
        block.setUserId(rs.getLong("user_id"));
        block.setEventType(rs.getString("event_type"));
        block.setOperation(rs.getString("operation"));
        block.setEntityId(rs.getLong("entity_id"));
        Timestamp timestamp = rs.getTimestamp("created_at");
        block.setCreatedAt(timestamp.toInstant());
        return block;
    }
}
