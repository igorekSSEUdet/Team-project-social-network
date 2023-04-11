package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getGenreList() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre")));
    }

    @Override
    public Genre getById(int id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        if(rows.next()) {
            return new Genre(id, rows.getString("genre"));
        } else throw new NoSuchElementException("Рейтинг не найден");
    }
}