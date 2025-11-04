package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.repositories.BookRepositoryCustomImpl;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class BookServiceImplTest {

    private static final Long GET_BOOK_ID = 1L;

    @Autowired
    private BookService bookService;

    @MockBean
    private BookRepositoryCustomImpl bookRepositoryCustomImpl;

    private Flux<BookResponseDto> books;

    @BeforeEach
    void setUp() {
        books = Flux.just(
            new BookResponseDto(1L, "Title_1",
                new AuthorDto(1L, "Author_1"), List.of(new GenreDto(1L, "Genre_1"))),
            new BookResponseDto(2L, "Title_2",
                new AuthorDto(2L, "Author_2"), List.of(new GenreDto(2L, "Genre_1")))
        );
    }

    @Test
    @DisplayName("должен загружать книгу по id")
    void shouldReturnBookById() {
        BookResponseDto book = new BookResponseDto(1L, "Title_1",
            new AuthorDto(1L, "Author_1"),
            List.of(new GenreDto(1L, "Genre_1"), new GenreDto(2L, "Genre_2")));

        when(bookRepositoryCustomImpl.findById(GET_BOOK_ID)).thenReturn(Mono.just(book));

        Mono<BookResponseDto> result = bookService.findById(GET_BOOK_ID);

        StepVerifier.create(result)
            .expectNextMatches(dto ->
                dto.getId().equals(1L) &&
                    dto.getTitle().equals("Title_1") &&
                    dto.getAuthor().getId().equals(1L) &&
                    dto.getAuthor().getFullName().equals("Author_1") &&
                    !dto.getGenres().isEmpty())
            .verifyComplete();
    }

    @Test
    @DisplayName("должен загружать все книги")
    void shouldReturnBooks() {
        when(bookRepositoryCustomImpl.findAll()).thenReturn((books));

        Flux<BookResponseDto> result = bookService.findAll();

        StepVerifier.create(result)
            .expectNextMatches(dto ->
                dto.getId().equals(1L) &&
                    dto.getTitle().equals("Title_1") &&
                    dto.getAuthor().getId().equals(1L) &&
                    dto.getAuthor().getFullName().equals("Author_1") &&
                    !dto.getGenres().isEmpty())
            .expectNextMatches(dto ->
                dto.getId().equals(2L) &&
                    dto.getTitle().equals("Title_2") &&
                    dto.getAuthor().getId().equals(2L) &&
                    dto.getAuthor().getFullName().equals("Author_2") &&
                    !dto.getGenres().isEmpty())
            .verifyComplete();
    }
}
