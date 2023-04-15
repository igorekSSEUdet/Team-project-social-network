package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getMpaList() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("name")));
    }

    @Override
    public Mpa getById(int id) {
        String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        if (rows.next()) {
            return new Mpa(id, rows.getString("name"));
        } else throw new NoSuchElementException("Рейтинг не найден");
    }
}