package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if(user.getName() == null) user.setName(user.getLogin());
        user.setId(users.size() + 1);
        users.put(user.getId(), user);
        log.debug("Успешно обработан запрос POST /users.");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if(users.containsKey(user.getId())) {
            if(user.getName() == null) user.setName(user.getLogin());
            users.put(user.getId(), user);
            log.debug("Успешно обработан запрос PUT /users.");
            return user;
        } else {
            log.error("Не удалось обработать запрос PUT /users.");
            throw new ValidationException();
        }
    }

    @GetMapping
    public List<User> getUsers() {
        log.debug("Получен запрос GET /users.");
        return new ArrayList<>(users.values());
    }
}