package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> getMpa() {
        return mpaStorage.getMpaList();
    }

    public Mpa getMpaById(int id) {
        return mpaStorage.getById(id);
    }
}