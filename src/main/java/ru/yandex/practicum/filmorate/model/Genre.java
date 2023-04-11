package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class Genre implements Comparable<Genre> {
    private Integer id;
    private String name;

    public Genre(Integer id) {
        this.id = id;
    }

    @Override
    public int compareTo(Genre o) {
        return this.id - o.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return Objects.equals(id, genre.id) && Objects.equals(name, genre.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}