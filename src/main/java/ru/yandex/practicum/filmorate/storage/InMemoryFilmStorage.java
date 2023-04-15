package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static int count = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        film.setId(++count);
        films.put(count, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(int id) {
        films.remove(id);
    }

    @Override
    public List<Film> getFilmsList() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(int id) {
        return films.get(id);
    }

    @Override
    public void addLike(int userId, int filmId) {
        films.get(filmId).getLikes().add(userId);
    }

    @Override
    public void removeLike(int userId, int filmId) {
        films.get(filmId).getLikes().remove(userId);
    }
}