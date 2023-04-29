package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventUtils {

    private final JdbcTemplate jdbcTemplate;

    public void addEvent(int userId, String eventType, String operation, int entityId) {
        String sql = "INSERT INTO events(timestamp,user_id,eventType,operation,entityId) " +
                "VALUES(?,?,?,?,?)";
        jdbcTemplate.update(sql,
                System.currentTimeMillis(),
                userId,
                eventType,
                operation,
                entityId
        );
    }

}
