package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
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
import java.util.stream.Collectors;

@Component
@Primary
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;
    private final EventStorage eventUtils;

    @Override
    public Film addFilm(Film film) {
        if (film.getMpa() != null) film.setMpa(mpaStorage.getById(film.getMpa().getId()));
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue());
        if (!film.getGenres().isEmpty()) {
            film.getGenres().forEach(genre -> jdbcTemplate.update("INSERT INTO films_genres VALUES (?,?)",
                    film.getId(), genre.getId()));
        }
        insertDirectors(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getMpa() != null) film.setMpa(mpaStorage.getById(film.getMpa().getId()));
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
        jdbcTemplate.update("DELETE FROM films_directors WHERE film_id = ?", film.getId());
        insertDirectors(film);
        return film;
    }

    @Override
    public List<Film> getFilmsList() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::createFilm);
    }

    @Override
    public Film getById(int id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, this::createFilm, id);
    }

    @Override
    public void addLike(int userId, int filmId) {
        String sql = "INSERT INTO likes VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
        eventUtils.addEvent(userId,"LIKE","ADD",filmId);
    }

    @Override
    public void removeLike(int userId, int filmId) {
        String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
        eventUtils.addEvent(userId,"LIKE","REMOVE",filmId);
    }

    @Override
    public void deleteFilm(int id) {
      String sql = "DELETE FROM films WHERE film_id = ?";
      jdbcTemplate.update(sql,id);
    }

    @Override
    public List<Film> getFilmsByDirectorWithYear(int id) {
        String sql = "SELECT films.*" +
                "FROM films " +
                "INNER JOIN films_directors AS fd on films.film_id = fd.film_id " +
                "WHERE fd.director_id = ? GROUP BY films.film_id ORDER BY release_date";
        return new ArrayList<>(jdbcTemplate.query(sql, this::createFilm, id));
    }

    @Override
    public List<Film> getFilmsByDirectorWithLikes(int id) {
        String sql = "SELECT films.*, COUNT(likes.user_id) AS rate " +
                "FROM films " +
                "LEFT JOIN likes ON films.film_id = likes.film_id " +
                "INNER JOIN films_directors AS fd on films.film_id = fd.film_id " +
                "WHERE fd.director_id = ? GROUP BY films.film_id ORDER BY rate";
        return new ArrayList<>(jdbcTemplate.query(sql, this::createFilm, id));
    }

    @Override
    public boolean isExists(int id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        return rows.next();
    }

    private Film createFilm(ResultSet resultSet, int row) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setDirectors(directorStorage.getDirectorsByFilmId(film.getId()));
        if (resultSet.getDate("release_date") != null)
            film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        if (resultSet.getInt("mpa_id") != 0)
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

    private void insertDirectors(Film film) {
        if (!film.getDirectors().isEmpty()) {
            film.getDirectors().forEach(director -> {
                if (directorStorage.getById(director.getId()) != null) {
                    jdbcTemplate.update("INSERT INTO films_directors VALUES (?, ?)",
                            film.getId(), director.getId());
                } else throw new NoSuchElementException("Режиссёр не найден");
            });
        }
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT FILM_ID FROM LIKES WHERE USER_ID = ? " +
                "INTERSECT " +
                "SELECT FILM_ID FROM LIKES WHERE USER_ID = ?";
        List<Integer> listOfCommonFilms = jdbcTemplate.queryForList(sql, new Object[]{userId, friendId}, Integer.class);
        return listOfCommonFilms.stream()
                .map(id -> getById(id))
                .sorted(Comparator.<Film>comparingInt(f -> f.getLikes().size()).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getFilmsByQuery(String query, List<String> by) {
        String sql = "SELECT * " +
                "FROM (SELECT f.FILM_ID, F.NAME, DESCRIPTION, RELEASE_DATE, " +
                "DURATION, MPA_ID, director_name, COUNT(USER_ID) AS COUNT " +
                "FROM FILMS f " +
                "LEFT JOIN FILMS_DIRECTORS FD on f.FILM_ID = FD.FILM_ID " +
                "LEFT JOIN DIRECTORS D on FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "LEFT JOIN LIKES L on f.FILM_ID = L.FILM_ID " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT DESC) ";

        String where = "WHERE ";
        int cnt = 0;
        if (by.stream().anyMatch(s -> s.equalsIgnoreCase("title"))) {
            where += "LOWER(NAME) LIKE ?";
            cnt++;
        }
        if (by.stream().anyMatch(s -> s.equalsIgnoreCase("director"))) {
            if (cnt > 0) {
                where += " OR ";
            }
            where += "LOWER(director_name) LIKE ?";
            cnt++;
        }

        if (cnt == 0) {
            throw new IllegalArgumentException("Поиск выполняется только по title и director");
        }

        String[] list = new String[cnt];
        Arrays.fill(list, "%" + query.toLowerCase() + "%");

        List<Film> films = jdbcTemplate.query(sql + where, this::createFilm, list);

        return films;
    }

    @Override
    public List<Film> getRecommendationForUser(int id) {
        /** Запрос находит 10 пользователей, которые иемют максимальное пересечением по лайкам
         *  с запрошенным пользователем.
         *  И затем возвращает список фильмов, которые лайкнули эти 10 юзеров,но не лайкнул запрошенный.
         *  Запрос конечно абсолютно не читаемый... Что с этим делать, я не знаю...
         */
        String sqlRecommendedFilms = new StringBuilder()
                .append("SELECT * FROM films f WHERE film_id IN")   // выбираем фильмы с id из найденного диапазона
                .append(" ( ")
                .append("SELECT film_id FROM likes WHERE user_id IN")   // выбираем id фильмов, которые смотрели "похожие" пользователи, но не смотрел запрошенный
                .append("    ( ")
                .append("    SELECT t2.user_id FROM likes AS t1 ")      // выбираем id пользователей, максимально пересекающихся по лайкам с запрошенным
                .append("    INNER JOIN likes AS t2 ON t1.film_id = t2.film_id AND t1.user_id = ? ")
                .append("    LEFT JOIN (SELECT user_id, COUNT(film_id) total FROM likes GROUP BY user_id) AS t3 ON t2.user_id = t3.user_id ")   //находим для каждого пользователя кол-во фильмов, которые он лайкнул
                .append("    GROUP BY t2.user_id, t3.total ")
                .append("    HAVING (t3.total - COUNT(t1.user_id)) > 0 ")   //отсеиваем тех, у кого лайки совпали полностью (нет фильмов для рекомендации)
                .append("    ORDER BY COUNT(t1.user_id) DESC ")     // сортируем по максимальному пересечению по лайкам
                .append("    LIMIT 10 ")        // оставляем только ТОП 10 похожих пользователей
                .append("    ) ")
                .append("EXCEPT ")      // выбираем разность множеств фильмов найденных пользователей
                .append("SELECT film_id FROM likes WHERE user_id = ?")      // и запрошенного
                .append(" );")
                .toString();
        return jdbcTemplate.query(sqlRecommendedFilms, this::createFilm, id, id);
    }
}