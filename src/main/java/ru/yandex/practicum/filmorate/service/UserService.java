package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.getById(user.getId()) != null) {
            if (user.getName() == null) user.setName(user.getLogin());
            return userStorage.updateUser(user);
        } else throw new NoSuchElementException();
    }

    public List<User> getUsers() {
        return userStorage.getUsersList();
    }

    public User getUserById(int id) {
        if (userStorage.getById(id) != null) {
            return userStorage.getById(id);
        } else throw new NoSuchElementException();
    }

    public void addFriend(int firstUser, int secondUser) {
        if (userStorage.getById(firstUser) != null && userStorage.getById(secondUser) != null) {
            userStorage.getById(firstUser).getFriends().add(secondUser);
            userStorage.getById(secondUser).getFriends().add(firstUser);
        } else throw new NoSuchElementException();
    }

    public void deleteFriend(int firstUser, int secondUser) {
        if (userStorage.getById(firstUser) != null && userStorage.getById(secondUser) != null) {
        userStorage.getById(firstUser).getFriends().remove(secondUser);
        userStorage.getById(secondUser).getFriends().remove(firstUser);
        } else throw new NoSuchElementException();
    }

    public List<User> getCommonFriends(int firstUser, int secondUser) {
        if (userStorage.getById(firstUser) != null && userStorage.getById(secondUser) != null) {
            List<Integer> commonFriendsIds = new ArrayList<>(userStorage.getById(firstUser).getFriends());
            commonFriendsIds.retainAll(userStorage.getById(secondUser).getFriends());
            List<User> commonFriends = new ArrayList<>();
            for (int commonFriendId : commonFriendsIds) {
                commonFriends.add(userStorage.getById(commonFriendId));
            }
            return commonFriends;
        } else throw new NoSuchElementException();
    }

    public List<User> getFriends(int id) {
        if (userStorage.getById(id) != null) {
            Set<Integer> friendsIds = userStorage.getById(id).getFriends();
            List<User> friends = new ArrayList<>();
            for (int friendsId : friendsIds) {
                friends.add(userStorage.getById(friendsId));
            }
            return friends;
        } else throw new NoSuchElementException();
    }
}