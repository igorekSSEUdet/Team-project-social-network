package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addFilm(Film film) {
        if (film.getReleaseDate().isAfter(MIN_DATE)) {
            return filmStorage.addFilm(film);
        } else throw new ValidationException();
    }

    public List<Film> getFilms() {
        return filmStorage.getFilmsList();
    }

    public Film updateFilm(Film film) {
        if(film.getReleaseDate().isAfter(MIN_DATE) && filmStorage.getById(film.getId()) != null) {
            return filmStorage.updateFilm(film);
        } else throw new NoSuchElementException();
    }

    public Film getFilmById(int id) {
        if(filmStorage.getById(id) != null) {
            return filmStorage.getById(id);
        } else throw new NoSuchElementException();
    }

    public void addLike(int filmId, int userId) {
        if(filmStorage.getById(filmId) != null && userStorage.getById(userId) != null) {
            filmStorage.getById(filmId).getLikes().add(userId);
        } else throw new NoSuchElementException();
    }

    public void removeLike(int filmId, int userId) {
        if(filmStorage.getById(filmId) != null && userStorage.getById(userId) != null) {
            filmStorage.getById(filmId).getLikes().remove(userId);
        } else throw new NoSuchElementException();
    }

    public List<Film> getPopular(int count) {
        List<Film> films = new ArrayList<>(filmStorage.getFilmsList());
        if(count != 1) count--;
        if(count > films.size()) count = films.size();
        films.sort((Comparator.comparingInt(o -> o.getLikes().size())));
        Collections.reverse(films);
        return films.subList(0,count);
    }
}