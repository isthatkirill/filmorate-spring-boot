package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long filmId, Long userId) {
        String query = "INSERT INTO films_likes (film_id, user_id) values (?, ?)";
        jdbcTemplate.update(query, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String query = "DELETE FROM films_likes WHERE film_id = ? and user_id = ?";
        jdbcTemplate.update(query, filmId, userId);
    }

    @Override
    public Set<Long> getLikesByFilmId(Long filmId) {
        String query = "SELECT user_id FROM films_likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(query, (rs, rowNum)
                -> makeLikeId(rs, rowNum), filmId));
    }

    private Long makeLikeId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("user_id");
    }
}