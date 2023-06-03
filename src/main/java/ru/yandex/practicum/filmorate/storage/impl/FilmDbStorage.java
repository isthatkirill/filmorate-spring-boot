package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.util.Mappers.FILM_MAPPER;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", Date.valueOf(film.getReleaseDate()));
        parameters.put("duration_minutes", film.getDuration());
        parameters.put("mpa_id", film.getMpa().getId());

        Number filmId = insert.executeAndReturnKey(parameters);
        film.setId(filmId.longValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String query = "UPDATE FILMS set name = ?, description = ?, release_date = ?, " +
                "duration_minutes = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String query = "SELECT films.*, mpa.name AS mpa_name " +
                "FROM films LEFT OUTER JOIN mpa ON films.mpa_id = mpa.mpa_id";
        return jdbcTemplate.query(query, FILM_MAPPER);
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        String query = "SELECT films.*, mpa.name AS mpa_name " +
                "FROM films LEFT OUTER JOIN mpa ON films.mpa_id = mpa.mpa_id WHERE film_id = ?";
        List<Film> films = jdbcTemplate.query(query, FILM_MAPPER, id);
        if (films.isEmpty()) {
            log.info("Film with id = {} not found", id);
            return Optional.empty();
        }
        log.info("Film found: {} {}", films.get(0).getId(), films.get(0).getName());
        return Optional.of(films.get(0));
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String query = "SELECT f.*, COUNT(fl.user_id) AS total_likes, mpa.name AS mpa_name\n" +
                "FROM films as f\n" +
                "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id\n" +
                "LEFT OUTER JOIN mpa ON f.mpa_id = mpa.mpa_id\n" +
                "GROUP BY f.film_id\n" +
                "ORDER BY total_likes DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(query, FILM_MAPPER, count);
    }

    @Override
    public List<Film> findFilmListDirectorById(long director) {
        String  sql = "SELECT films.*, mpa.name AS mpa_name " +
                "FROM films LEFT OUTER JOIN mpa ON films.mpa_id = mpa.mpa_id WHERE film_id IN " +
                "(SELECT film_id FROM FILM_DIRECTORS WHERE DIRECTOR_ID =?)";

        List<Film> filmsDirectorList = jdbcTemplate.query(sql,FILM_MAPPER, director);
        return filmsDirectorList;
    }

    @Override
    public List<Film> getCommonFilmsByUsers(Long userId, Long friendId) {
        String query = "SELECT f.*, COUNT(fl.user_id) AS total_likes, mpa.name AS mpa_name\n" +
                "FROM films as f\n" +
                "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id\n" +
                "LEFT OUTER JOIN mpa ON f.mpa_id = mpa.mpa_id\n" +
                "WHERE f.film_id IN (SELECT film_id FROM films_likes WHERE user_id = ?\n" +
                "INTERSECT\n" +
                "SELECT film_id FROM films_likes  WHERE user_id = ?)\n" +
                "GROUP BY f.film_id\n" +
                "ORDER BY total_likes DESC";
        return jdbcTemplate.query(query, FILM_MAPPER, userId, friendId);
    }
}
