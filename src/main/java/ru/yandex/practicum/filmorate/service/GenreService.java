package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> getAllGenres() {
        log.info("Get all genres");
        return genreStorage.getAllGenres();
    }

    public Genre findGenreById(Integer id) {
        log.info("Get genre by id = {}", id);
        return checkGenreExistent(id);
    }

    private Genre checkGenreExistent(Integer id) {
        return genreStorage
                .findGenreById(id)
                .orElseThrow(() -> new EntityNotFoundException(Genre.class, "Id: " + id));
    }
}
