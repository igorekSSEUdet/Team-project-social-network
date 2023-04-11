package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Data
public class User {
    private int id;
    @Email(message = "Некорректный формат email")
    private String email;
    @NotNull(message = "Нет логина")
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    private String name;
    @Past(message = "Некорректная дата рождения")
    private LocalDate birthday;
    private Set<Integer> friends = new TreeSet<>();

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("login", login);
        map.put("name", name);
        map.put("birthday", birthday);
        return map;
    }
}