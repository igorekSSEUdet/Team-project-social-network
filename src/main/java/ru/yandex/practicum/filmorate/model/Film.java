package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class Film {
    private static int count = 0;
    private int id = ++count;
    @NotNull
    @NotBlank
    private String name;
    @Length(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Min(0)
    private int duration;
}