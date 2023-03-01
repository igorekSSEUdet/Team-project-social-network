package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        if(user.getName() == null) user.setName(user.getLogin());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        if(users.containsKey(user.getId())) {
            if(user.getName() == null) user.setName(user.getLogin());
            users.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException();
        }
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}