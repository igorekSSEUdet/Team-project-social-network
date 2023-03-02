package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static int count = 0;
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@RequestBody @Valid  Film film) {
        if(film.getReleaseDate().isAfter(MIN_DATE)) {
            film.setId(++count);
            films.put(film.getId(), film);
            log.debug("Успешно обработан запрос POST /films.");
            return film;
        } else {
            log.error("Не удалось обработать запрос POST /films.");
            throw new ValidationException();
        }
    }

    @GetMapping
    public List<Film> getFilms() {
        log.debug("Получен запрос GET /films.");
        return new ArrayList<>(films.values());
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        if(film.getReleaseDate().isAfter(MIN_DATE) && films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Успешно обработан запрос PUT /films.");
            return film;
        } else {
            log.error("Не удалось обработать запрос PUT /films");
            throw new ValidationException();
        }
    }
}