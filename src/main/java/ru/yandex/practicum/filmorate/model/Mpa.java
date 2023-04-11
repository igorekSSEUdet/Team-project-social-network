package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Mpa {
    @NotNull(message = "Необходим рейтинг")
    private Integer id;
    private String name;

    public Mpa(Integer id) {
        this.id = id;
    }
}