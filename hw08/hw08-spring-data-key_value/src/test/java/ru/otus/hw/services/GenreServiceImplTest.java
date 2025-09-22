package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с жанрами на основе MongoDB репозитория")
@DataMongoTest
@Import(GenreServiceImpl.class)
@ActiveProfiles("test")
public class GenreServiceImplTest {

    @Autowired
    GenreServiceImpl genreService;

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

        var returnedGenres = genreService.findAll();

        assertThat(returnedGenres).usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedGenres);
    }

    @Test
    @DisplayName("должен загружать все жанры по переданному набору id")
    void shouldReturnGenreListByIds() {
        var genre = genreRepository.save(new Genre(null, "Genre_1"));
        var genre3 = genreRepository.save(new Genre(null, "Genre_3"));
        var expectedGenres = Arrays.asList(genre, genre3);
        Set<String> genresIds = Stream.of(genre.getId(), genre3.getId())
            .collect(Collectors.toSet());

        var returnedGenres = genreService.findAllByIdIn(genresIds);

        assertThat(returnedGenres).usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedGenres);
    }
}
