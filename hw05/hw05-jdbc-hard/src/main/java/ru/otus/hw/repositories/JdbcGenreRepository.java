package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public List<Genre> findAll() {
        return jdbcOperations.query("""
                SELECT id,
                       name
                FROM genres
                """,
            new GenreRowMapper());
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Genres ids must not be null or empty");
        }
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("ids", ids);

        return jdbcOperations.query("""
                SELECT id,
                       name
                FROM genres
                WHERE id IN (:ids)
                """,
            parameters,
            new GenreRowMapper());
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            return new Genre(rs.getLong("id"), rs.getString("name"));
        }
    }
}
