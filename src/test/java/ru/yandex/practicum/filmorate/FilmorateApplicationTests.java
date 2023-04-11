package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private User user2;
    private Film film2;

    @BeforeEach
    public void beforeEach() {
        User user1 = new User();
        user1.setEmail("email@email.com");
        user1.setLogin("login");
        user1.setName("name");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.addUser(user1);
        user2 = new User();
        user2.setEmail("email@email.com");
        user2.setLogin("login");
        user2.setName("name");
        user2.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.addUser(user2);
        Film film1 = new Film();
        film1.setName("name");
        film1.setDescription("description");
        film1.setDuration(100);
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setMpa(new Mpa(1));
        filmStorage.addFilm(film1);
        film2 = new Film();
        film2.setName("name");
        film2.setDescription("description");
        film2.setDuration(100);
        film2.setReleaseDate(LocalDate.of(2000, 1, 1));
        film2.setMpa(new Mpa(1));
        film2.setGenres(new TreeSet<>(Set.of(new Genre(1))));
        filmStorage.addFilm(film2);
}

    @Test
    public void getUserById() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.getById(1));
        assertThat(userOptional).isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(user).hasFieldOrPropertyWithValue("login", "login");
                    assertThat(user).hasFieldOrPropertyWithValue("name", "name");
                    assertThat(user).hasFieldOrPropertyWithValue(
                            "birthday", LocalDate.of(2000, 1, 1));
                });
    }

    @Test
    public void updateUser() {
        user2.setId(2);
        user2.setLogin("login2");
        user2.setEmail("email2@email.com");
        user2.setName("name2");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        Optional<User> userUpdated = Optional.ofNullable(userStorage.updateUser(user2));
        assertThat(userUpdated).isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", 2);
                    assertThat(user).hasFieldOrPropertyWithValue("email", "email2@email.com");
                    assertThat(user).hasFieldOrPropertyWithValue("login", "login2");
                    assertThat(user).hasFieldOrPropertyWithValue("name", "name2");
                    assertThat(user)
                            .hasFieldOrPropertyWithValue(
                                    "birthday", LocalDate.of(2002, 2, 2));
                });
    }

    @Test
    public void getFilmById() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getById(1));
        assertThat(filmOptional).isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("name", "name");
                    assertThat(film).hasFieldOrPropertyWithValue("description", "description");
                    assertThat(film).hasFieldOrPropertyWithValue("duration", 100);
                    assertThat(film).hasFieldOrPropertyWithValue(
                            "releaseDate", LocalDate.of(2000, 1, 1));
                    assertThat(film).hasFieldOrPropertyWithValue("mpa", new Mpa(1, "G"));
                });
    }

    @Test
    public void updateFilm() {
        film2.setId(2);
        film2.setName("name2");
        film2.setDescription("description2");
        film2.setDuration(120);
        film2.setReleaseDate(LocalDate.of(2002, 2, 2));
        film2.setMpa(new Mpa(2));
        film2.setGenres(new TreeSet<>(Set.of(new Genre(2))));
        Optional<Film> filmUpdated = Optional.ofNullable(filmStorage.updateFilm(film2));
        assertThat(filmUpdated).isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("name", "name2");
                    assertThat(film).hasFieldOrPropertyWithValue("description", "description2");
                    assertThat(film).hasFieldOrPropertyWithValue("duration", 120);
                    assertThat(film).hasFieldOrPropertyWithValue(
                            "releaseDate", LocalDate.of(2002, 2, 2));
                    assertThat(film).hasFieldOrPropertyWithValue("mpa", new Mpa(2, "PG"));
                    assertThat(film).hasFieldOrPropertyWithValue(
                                    "genres", new TreeSet<>(Set.of(new Genre(2, "Драма"))));
                });
    }

    @Test
    public void shouldAddAndDeleteLike() {
        filmStorage.addLike(1, 1);
        Optional<Film> filmWithLike = Optional.ofNullable(filmStorage.getById(1));
        assertThat(filmWithLike)
                .hasValueSatisfying(film -> assertThat(film)
                        .hasFieldOrPropertyWithValue("likes", new TreeSet<>(Set.of(1))));
        filmStorage.removeLike(1, 1);
        Optional<Film> filmWithoutLike = Optional.ofNullable(filmStorage.getById(1));
        assertThat(filmWithoutLike).hasValueSatisfying(film ->
            assertThat(film).hasFieldOrPropertyWithValue("likes", new TreeSet<Integer>()));
    }

    @Test
    public void shouldAddAndDeleteFriend() {
        userStorage.addFriend(2, 1);
        Optional<User> user2Optional = Optional.ofNullable(userStorage.getById(2));
        assertThat(user2Optional).hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("friends", new TreeSet<>(Set.of(1))));
        Optional<User> user1Optional = Optional.ofNullable(userStorage.getById(1));
        assertThat(user1Optional).hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("friends", new TreeSet<>()));
        userStorage.deleteFriend(2, 1);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getById(2));
        assertThat(userOptional).hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("friends", new TreeSet<>()));
    }

    @Test
    public void getMpaList() {
        Optional<List<Mpa>> mpaList = Optional.of(mpaStorage.getMpaList());
        assertThat(mpaList).isPresent();
        assertEquals(5, mpaList.get().size());
    }

    @Test
    public void getGenresList() {
        Optional<List<Genre>> genreList = Optional.of(genreStorage.getGenreList());
        assertThat(genreList).isPresent();
        assertEquals(6, genreList.get().size());
    }
}