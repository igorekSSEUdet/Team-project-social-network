package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.debug("Получен запрос POST /users.\n" + user.toString());
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Получен запрос PUT /users.\n" + user.toString());
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        log.debug("Получен запрос GET /users.");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable int userId) {
        log.debug(String.format("Получен запрос GET users/%d", userId));
        return userService.getUserById(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.debug(String.format("Получен запрос PUT users/%d/friends/%d", id, friendId));
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.debug(String.format("Получен запрос DELETE users/%d/friends/%d", id, friendId));
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.debug(String.format("Получен запрос GET users/%d/friends", id));
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.debug(String.format("Получен запрос GET users/%d/friends/common/%d", id, otherId));
        return userService.getCommonFriends(id, otherId);
    }
}