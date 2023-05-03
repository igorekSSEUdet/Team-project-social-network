package ru.yandex.practicum.filmorate.storage;

public interface EventStorage {
    void addEvent(int userId, String eventType, String operation, int entityId);
}
