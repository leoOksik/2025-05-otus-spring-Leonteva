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
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.BookService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(controllers = BookController.class)
@ActiveProfiles("test")
public class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookService bookService;

    private Flux<BookResponseDto> books;

    @BeforeEach
    void setUp() {
        books = getBooks();
    }

    @DisplayName("должен возвращать книги")
    @Test
    void shouldReturnCorrectlyBooks()  {
        given(bookService.findAll()).willReturn(books);

        var result = webTestClient.get()
            .uri("/api/v1/books")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .returnResult(BookResponseDto.class)
            .getResponseBody();

        StepVerifier.create(result)
            .expectNextMatches(bookDto -> bookDto.getTitle().equals("Title_test_1"))
            .expectNextMatches(bookDto -> bookDto.getTitle().equals("Title_test_2"))
            .verifyComplete();
    }

    @DisplayName("должен возвращать книгу по id")
    @Test
    void shouldReturnCorrectlyBookById()  {
      BookResponseDto bookResponseDto = new BookResponseDto(1L, "Title_test", new AuthorDto(), new ArrayList<>());
        given(bookService.findById(1L)).willReturn(Mono.just(bookResponseDto));

        Mono<BookResponseDto> result = webTestClient.get()
            .uri("/api/v1/books/{id}", 1L)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .returnResult(BookResponseDto.class)
            .getResponseBody()
            .single();

        StepVerifier.create(result)
            .assertNext(bookDto -> assertThat(bookDto)
                .isNotNull()
                .extracting(BookResponseDto::getId,BookResponseDto::getTitle)
                .containsExactly(1L, "Title_test"))
            .verifyComplete();
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldCorrectlySaveNewBook() {
        AuthorDto author = new AuthorDto(1L, "Author_test_1");
        BookRequestDto requestDto = new BookRequestDto(null, "Title_test_1", 1L, Set.of());
        BookResponseDto responseDto = new BookResponseDto(1L, "Title_test_1", author, new ArrayList<>());
        given(bookService.insert(any(Mono.class))).willReturn(Mono.just(responseDto));

         BookResponseDto  bookResponseDto =  webTestClient.post()
            .uri("/api/v1/books")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody(BookResponseDto.class)
            .returnResult().getResponseBody();

        assertThat(bookResponseDto)
            .isNotNull()
            .satisfies(book -> {
                assertThat(book.getId()).isEqualTo(1L);
                assertThat(book.getTitle()).isEqualTo("Title_test_1");
                assertThat(book.getAuthor().getId()).isEqualTo(1L);
                assertThat(book.getAuthor().getFullName()).isEqualTo("Author_test_1");
            });
    }

    @DisplayName("должен обновлять существующую книгу")
    @Test
    void shouldCorrectlyEditBook() {
        AuthorDto author = new AuthorDto(1L, "Author_test_1");
        BookRequestDto requestDto = new BookRequestDto(1L, "Title_test_1", 1L, Set.of());
        BookResponseDto responseDto = new BookResponseDto(1L, "Title_test_1", author, new ArrayList<>());
        given(bookService.update(eq(1L), any(Mono.class))).willReturn(Mono.just(responseDto));

        BookResponseDto  bookResponseDto =  webTestClient.put()
            .uri("/api/v1/books/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody(BookResponseDto.class)
            .returnResult().getResponseBody();

        assertThat(bookResponseDto)
            .isNotNull()
            .satisfies(book -> {
                assertThat(book.getId()).isEqualTo(1L);
                assertThat(book.getTitle()).isEqualTo("Title_test_1");
                assertThat(book.getAuthor().getId()).isEqualTo(1L);
                assertThat(book.getAuthor().getFullName()).isEqualTo("Author_test_1");
            });
    }

    @DisplayName("должен удалять существующую книгу")
    @Test
    void shouldCorrectlyDeleteBook() {
        given(bookService.deleteById(2L)).willReturn(Mono.empty());

        webTestClient.delete()
            .uri("/api/v1/books/{id}", 2L)
            .exchange()
            .expectStatus().isNoContent();
    }

    @DisplayName("должен возвращать статуc 404 и сообщение \"Book not found\", " +
        "если книга не найдена по переданному id при обновлении")
    @Test
    void shouldReturnExpectedErrorWhenBooksNotFoundForUpdate() {
        given(bookService.update(eq(7L), any(Mono.class)))
            .willReturn(Mono.error(new NotFoundException("Book not found")));

        webTestClient.put()
            .uri("/api/v1/books/{id}", 7L)
            .contentType(APPLICATION_JSON)
            .bodyValue(new BookRequestDto(7L, "test_title", 1L, Set.of()))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(String.class)
            .value(message -> assertThat(message).isEqualTo("Book not found"));
    }

    @DisplayName("должен возвращать статуc 404 и сообщение \"Book not found\", " +
        "если книга не найдена по переданному id при удалении")
    @Test
    void shouldReturnExpectedErrorWhenBooksNotFoundForDelete()  {
        willThrow(new NotFoundException("Book not found"))
            .given(bookService)
            .deleteById(7L);

        webTestClient.delete()
            .uri("/api/v1/books/{id}", 7L)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(String.class)
            .value(message -> assertThat(message).isEqualTo("Book not found"));
    }

    private Flux<BookResponseDto> getBooks() {
        AuthorDto author = new AuthorDto();
        List<GenreDto> genres = new ArrayList<>();

        return Flux.just(
            new BookResponseDto(1L, "Title_test_1", author, genres),
            new BookResponseDto(2L, "Title_test_2", author, genres));
    }
}
