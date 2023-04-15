package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static int count = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setId(++count);
        users.put(count, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int id) {
        users.remove(id);
    }

    @Override
    public List<User> getUsersList() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(int id) {
        return users.get(id);
    }

    @Override
    public void addFriend(int first, int second) {
        users.get(first).getFriends().add(users.get(second).getId());
        users.get(second).getFriends().add(users.get(first).getId());
    }

    @Override
    public void deleteFriend(int first, int second) {
        users.get(first).getFriends().remove(users.get(second).getId());
        users.get(second).getFriends().remove(users.get(first).getId());
    }
}