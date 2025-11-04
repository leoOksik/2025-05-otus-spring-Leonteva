package ru.otus.hw.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.otus.hw.controllers.GenreController;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import static org.mockito.BDDMockito.given;

@WebFluxTest(controllers = GenreController.class)
@ActiveProfiles("test")
public class GenreControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GenreService genreService;

    private Flux<GenreDto> genres;

    @BeforeEach
    void setUp() {
        genres = Flux.just(
            new GenreDto(1L, "Genre_1"),
            new GenreDto(2L, "Genre_2"),
            new GenreDto(3L, "Genre_3")
        );
    }

    @Test
    void shouldReturnCorrectlyAuthors() {
        given(genreService.findAll()).willReturn(genres);

        var result = webTestClient.get()
            .uri("/api/v1/genres")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .returnResult(GenreDto.class)
            .getResponseBody();

        StepVerifier.create(result)
            .expectNextMatches(genreDto -> genreDto.getName().equals("Genre_1"))
            .expectNextMatches(genreDto -> genreDto.getName().equals("Genre_2"))
            .expectNextMatches(genreDto -> genreDto.getName().equals("Genre_3"))
            .verifyComplete();
    }
}
