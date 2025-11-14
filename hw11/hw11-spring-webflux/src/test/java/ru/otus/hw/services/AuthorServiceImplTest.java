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
import ru.otus.hw.dto.AuthorDto;

import reactor.test.StepVerifier;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class AuthorServiceImplTest {

    private static final Long GET_AUTHOR_ID = 1L;

    @Autowired
    private AuthorService authorService;

    @MockBean
    private AuthorRepository authorRepository;

    private Flux<Author> authors;

    @BeforeEach
    void setUp() {
        authors = Flux.just(
            new Author(1L, "Author_1"),
            new Author(2L, "Author_2"),
            new Author(2L, "Author_3")
        );
    }

    @Test
    @DisplayName("должен загружать автора по id")
    void shouldReturnAuthorById() {
        Author author = new Author(1L, "Author_1");
        when(authorRepository.findById(GET_AUTHOR_ID)).thenReturn(Mono.just(author));

        Mono<AuthorDto> result = authorService.findById(GET_AUTHOR_ID);

        StepVerifier.create(result)
            .expectNextMatches(dto ->
                dto.getId().equals(1L) && dto.getFullName().equals("Author_1"))
            .verifyComplete();
    }

    @Test
    @DisplayName("должен загружать всех авторов")
    void shouldReturnAuthors() {
        when(authorRepository.findAll()).thenReturn((authors));

        Flux<AuthorDto> result = authorService.findAll();

        StepVerifier.create(result)
            .expectNextMatches(dto -> dto.getFullName().equals("Author_1"))
            .expectNextMatches(dto -> dto.getFullName().equals("Author_2"))
            .expectNextMatches(dto -> dto.getFullName().equals("Author_3"))
            .verifyComplete();
    }
}
