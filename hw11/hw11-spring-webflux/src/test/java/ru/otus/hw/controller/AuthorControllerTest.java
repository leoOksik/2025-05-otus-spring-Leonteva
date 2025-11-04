package ru.otus.hw.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.controllers.AuthorController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.AuthorService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(controllers = AuthorController.class)
@ActiveProfiles("test")
public class AuthorControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthorService authorService;

    private Flux<AuthorDto> authors;

    @BeforeEach
    void setUp() {
        authors = Flux.just(
            new AuthorDto(1L, "Author_1"),
            new AuthorDto(2L, "Author_2"),
            new AuthorDto(3L, "Author_3")
        );
    }

    @DisplayName("должен возвращать авторов")
    @Test
    void shouldReturnCorrectlyAuthors() {
        given(authorService.findAll()).willReturn(authors);

        var result = webTestClient.get()
            .uri("/api/v1/authors")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .returnResult(AuthorDto.class)
            .getResponseBody();

        StepVerifier.create(result)
            .expectNextMatches(authorDto -> authorDto.getFullName().equals("Author_1"))
            .expectNextMatches(authorDto -> authorDto.getFullName().equals("Author_2"))
            .expectNextMatches(authorDto -> authorDto.getFullName().equals("Author_3"))
            .verifyComplete();
    }

    @DisplayName("должен возвращать автора по id")
    @Test
    void shouldReturnCorrectlyAuthorById() {
        AuthorDto author = new AuthorDto(1L, "Author_1");
        given(authorService.findById(1L)).willReturn(Mono.just(author));

        Mono<AuthorDto> result = webTestClient.get()
            .uri("/api/v1/authors/{id}", 1L)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .returnResult(AuthorDto.class)
            .getResponseBody()
            .single();

        StepVerifier.create(result)
            .assertNext(authorDto -> assertThat(authorDto)
                .isNotNull()
                .extracting(AuthorDto::getId, AuthorDto::getFullName)
                .containsExactly(1L, "Author_1"))
            .verifyComplete();
    }

    @DisplayName("должен сохранять нового автора")
    @Test
    void shouldCorrectlySaveNewAuthor()  {

        AuthorDto author = new AuthorDto(null, "Author_3");
        AuthorDto savedAuthor = new AuthorDto(3L, "Author_3");
        given(authorService.insert(any(Mono.class))).willReturn(Mono.just(savedAuthor));

        AuthorDto dto = webTestClient.post()
            .uri("/api/v1/authors")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(author)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody(AuthorDto.class)
            .returnResult()
            .getResponseBody();

        assertThat(dto)
            .isNotNull()
            .extracting(AuthorDto::getId, AuthorDto::getFullName)
            .containsExactly(3L, "Author_3");
    }

    @DisplayName("должен обновлять существующего автора")
    @Test
    void shouldCorrectlyEditAuthor()  {
        AuthorDto author = new AuthorDto(1L, "Edit_Author_1");
        given(authorService.update(eq(1L), any(Mono.class))).willReturn(Mono.just(author));

        AuthorDto updatedAuthor = webTestClient.put()
            .uri("/api/v1/authors/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(author)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody(AuthorDto.class)
            .returnResult()
            .getResponseBody();

        assertThat(updatedAuthor)
            .isNotNull()
            .extracting(AuthorDto::getId, AuthorDto::getFullName)
            .containsExactly(1L, "Edit_Author_1");
    }

    @DisplayName("должен удалять существующего автора")
    @Test
    void shouldCorrectlyDeleteAuthor() {
        given(authorService.deleteById(2L)).willReturn(Mono.empty());

        webTestClient.delete()
            .uri("/api/v1/authors/{id}", 2L)
            .exchange()
            .expectStatus().isNoContent();
    }

    @DisplayName("должен возвращать статуc 404 и сообщение \"Author not found\", " +
        "если автор не найден по переданному id при обновлении")
    @Test
    void shouldReturnExpectedErrorWhenAuthorsNotFoundForUpdate() {
        given(authorService.update(eq(7L), any(Mono.class)))
            .willReturn(Mono.error(new NotFoundException("Author not found")));

        webTestClient.put()
            .uri("/api/v1/authors/{id}", 7L)
            .contentType(APPLICATION_JSON)
            .bodyValue(new AuthorDto(7L, "test_author"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(String.class)
            .value(message -> assertThat(message).isEqualTo("Author not found"));

    }

    @DisplayName("должен возвращать статуc 404 и сообщение \"Author not found\", " +
        "если автор не найден по переданному id при удалении")
    @Test
    void shouldReturnExpectedErrorWhenAuthorsNotFoundForDelete()  {
        willThrow(new NotFoundException("Author not found"))
            .given(authorService)
            .deleteById(7L);

        webTestClient.delete()
            .uri("/api/v1/authors/{id}", 7L)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(String.class)
            .value(message -> assertThat(message).isEqualTo("Author not found"));
    }
}
