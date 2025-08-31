package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами")
@JdbcTest
@Import(JdbcGenreRepository.class)
public class JdbcGenreRepositoryTest {
    @Autowired
    JdbcGenreRepository jdbcGenreRepository;

    private List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @Test
    @DisplayName("должен загружать список всех жанров")
    void shouldReturnCorrectGenreList() {
        var actualGenres = jdbcGenreRepository.findAll();
        var expectedGenres = dbGenres;

        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
    }

    @Test
    @DisplayName("должен загружать все жанры по переданному набору id")
    void shouldReturnCorrectGenreListByIds() {
        var actualGenresByIds = jdbcGenreRepository.findAllByIds(getGenreIdsSet());
        var expectedGenresByIds = dbGenres.stream()
            .filter(genre -> genre.getId() % 2 == 0)
            .collect(Collectors.toList());

        assertThat(actualGenresByIds).containsExactlyElementsOf(expectedGenresByIds);
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
            .map(id -> new Genre(id.longValue(), "Genre_" + id))
            .toList();
    }

    private static Set<Long> getGenreIdsSet() {
        return LongStream.range(1, 7).boxed()
            .filter(id -> id % 2 == 0).collect(Collectors.toSet());
    }
}
