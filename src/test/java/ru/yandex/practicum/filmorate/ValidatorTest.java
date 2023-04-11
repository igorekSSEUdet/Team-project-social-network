package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ValidatorTest {
    @Autowired
    private LocalValidatorFactoryBean validator;
    @Autowired
    private FilmController filmController;
    private User user;
    private Film film;

    @BeforeEach
    void beforeEach() {
        film = new Film();
        film.setName("test");
        film.setDuration(10);
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDescription("test");
        user = new User();
        user.setName("test");
        user.setLogin("test");
        user.setEmail("email@email.ru");
        user.setBirthday(LocalDate.of(2000,1,1));
    }

    @Test
    void shouldShowViolationForFilmWithNoName() {
        film.setName(null);
        Set<ConstraintViolation<Film>> violations = validator.validateProperty(film, "name");
        List<String> actual = violations.stream().map(ConstraintViolation::getMessage)
                .sorted(Comparator.comparingInt(String::length)).collect(Collectors.toList());
        assertEquals(new ArrayList<>(List.of("Нет названия фильма", "Название фильма не может быть пустым")), actual);
    }

    @Test
    void shouldShowViolationForFilmWithNegativeDuration() {
        film.setDuration(-10);
        Set<ConstraintViolation<Film>> violations = validator.validateProperty(film, "duration");
        assertEquals(new ArrayList<>(List.of("Продолжительность не может быть отрицательной")),
                violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()));
    }

    @Test
    void shouldShowViolationForFilmWithLongDescription() {
        film.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        Set<ConstraintViolation<Film>> violations = validator.validateProperty(film, "description");
        assertEquals(new ArrayList<>(List.of("Превышен лимит описания")),
                violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()));
    }

    @Test
    void shouldThrowForFilmBefore1895() {
        film.setReleaseDate(LocalDate.of(1800,1,1));
        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void shouldShowViolationForIncorrectUserEmail() {
        user.setEmail("aaaaaa@");
        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "email");
        assertEquals(new ArrayList<>(List.of("Некорректный формат email")),
                violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()));
    }

    @Test
    void shouldShowViolationForUserWithNoLogin() {
        user.setLogin(null);
        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "login");
        List<String> expected = new ArrayList<>(List.of("Нет логина", "Логин не может быть пустым"));
        List<String> actual = violations.stream().map(ConstraintViolation::getMessage)
                .sorted(Comparator.comparingInt(String::length)).collect(Collectors.toList());
        assertEquals(expected, actual);
    }

    @Test
    void shouldShowViolationForUserWithBirthdayInFuture() {
        user.setBirthday(LocalDate.of(2300,1,1));
        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "birthday");
        assertEquals(new ArrayList<>(List.of("Некорректная дата рождения")),
                violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()));
    }
}