package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Genre;

import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Mongo для работы с жанрами")
@DataMongoTest
@ActiveProfiles("test")
public class MongoGenreRepositoryTest {

    @Autowired
    GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        genreRepository.deleteAll();
    }

    @Test
    @DisplayName("должен загружать список всех жанров")
    void shouldReturnGenreList() {
        var genre = genreRepository.save(new Genre(null, "Genre_1"));
        var genre2 = genreRepository.save(new Genre(null, "Genre_2"));
        var genre3 = genreRepository.save(new Genre(null, "Genre_3"));
        var expectedGenres = Arrays.asList(genre, genre2, genre3);

        var returnedGenres = genreRepository.findAll();

        assertThat(returnedGenres).usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedGenres);
    }

    @Test
    @DisplayName("должен загружать все жанры по переданному набору id")
    void shouldReturnGenreListByIds() {
        var genre = genreRepository.save(new Genre(null, "Genre_1"));
        var genre3 = genreRepository.save(new Genre(null, "Genre_3"));
        var expectedGenres = Arrays.asList(genre,genre3);
        var genresIds = Set.of(genre.getId(), genre3.getId());

        var returnedGenres = genreRepository.findAllByIdIn(genresIds);

        assertThat(returnedGenres).usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedGenres);
    }
}
