package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@RequestBody @Valid  Film film) {
        if(film.getReleaseDate().isAfter(MIN_DATE)) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException();
        }
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        if(film.getReleaseDate().isAfter(MIN_DATE) && films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException();
        }
    }
}