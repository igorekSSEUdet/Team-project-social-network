package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
//@RequestMapping("/users")
@RequiredArgsConstructor
public class RecommendationController {
    private final FilmService filmService;

    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendationForUser(@PathVariable int id) {
        log.debug(String.format("Получен запрос GET /users/%d/recommendations", id));
        return filmService.getRecommendationForUser(id);
    }
}
