package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class GenreServiceImplTest {

    @Autowired
    private GenreService genreService;

    @MockBean
    private GenreRepository genreRepository;

    private Flux<Genre> genres;

    @BeforeEach
    void setUp() {
        genres = Flux.just(
            new Genre(1L, "Genre_1"),
            new Genre(2L, "Genre_2")
        );
    }

    @Test
    @DisplayName("должен загружать все жанры")
    void shouldReturnGenres() {
        when(genreRepository.findAll()).thenReturn((genres));

        Flux<GenreDto> result = genreService.findAll();

        StepVerifier.create(result)
            .expectNextMatches(dto -> dto.getName().equals("Genre_1"))
            .expectNextMatches(dto -> dto.getName().equals("Genre_2"))
            .verifyComplete();
    }
}

