package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;

    @Override
    public Film addFilm(Film film) {
        film.setMpa(mpaStorage.getById(film.getMpa().getId()));
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.setId(id);
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO films_genres VALUES (?,?)", film.getId(), genre.getId());
            }
        }
        jdbcTemplate.update("UPDATE films SET mpa_id = ? WHERE film_id = ?",
                film.getMpa().getId(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        film.setMpa(mpaStorage.getById(film.getMpa().getId()));
        String sql = "UPDATE films SET film_id = ?, name = ?, description = ?," +
                " release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?", film.getId());
        if (!film.getGenres().isEmpty()) {
            film.getGenres().forEach((genre) -> jdbcTemplate.update("INSERT INTO films_genres VALUES (?, ?)",
                    film.getId(), genre.getId()));
            }
        return film;
    }

    @Override
    public List<Film> getFilmsList() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::createFilm);
    }

    @Override
    public Film getById(int id) {
        if (isExists(id)) {
            String sql = "SELECT * FROM films WHERE film_id = ?";
            return jdbcTemplate.queryForObject(sql, this::createFilm, id);
        } else return null;
    }

    @Override
    public void addLike(int userId, int filmId) {
            String sql = "INSERT INTO likes VALUES (?, ?)";
            jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void removeLike(int userId, int filmId) {
            String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
            jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void deleteFilm(int id) {
        throw new NotYetImplementedException("Не поддерживается");
    }

    private Film createFilm(ResultSet resultSet, int row) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setMpa(mpaStorage.getById(resultSet.getInt("mpa_id")));
        String sqlGenres =
                "SELECT films_genres.genre_id, genre FROM films_genres " +
                        "JOIN genres on films_genres.genre_id = genres.genre_id WHERE film_id = ?" +
                        " ORDER BY films_genres.genre_id";
        List<Genre> genres = jdbcTemplate.query(sqlGenres, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("genre")), film.getId());
        if (!genres.isEmpty()) film.setGenres(new TreeSet<>(genres));
        String sqlLikes = "SELECT user_id FROM likes WHERE film_id = ?";
        film.getLikes().addAll(jdbcTemplate.query(sqlLikes,
                (rs, rowNum) -> rs.getInt("user_id"), film.getId()));
        return film;
    }

    private boolean isExists(int id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        return rows.next();
    }
}