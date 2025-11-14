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
import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.CommentService;

import static org.mockito.BDDMockito.given;

@WebFluxTest(controllers = CommentController.class)
@ActiveProfiles("test")
public class CommentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CommentService commentService;

    private Flux<CommentDto> comments;

    @BeforeEach
    void setUp() {
        Book book = new Book(1L, "Title_test", 2L);
        comments = Flux.just(new CommentDto(1L, "Comment_1", book.getId()),
            new CommentDto(2L, "Comment_2", book.getId()));
    }

    @DisplayName("должен возвращать комментарии по id книги")
    @Test
    void shouldReturnCorrectlyCommentsByBookId() {
        given(commentService.findByBookId(1L)).willReturn(comments);

        var result = webTestClient.get()
            .uri("/api/v1/comments/book/{id}", 1L)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .returnResult(CommentDto.class)
            .getResponseBody();

        StepVerifier.create(result)
            .expectNextMatches(commentDto -> commentDto.getText().equals("Comment_1"))
            .expectNextMatches(commentDto -> commentDto.getText().equals("Comment_2"))
            .verifyComplete();
    }

    @DisplayName("должен удалять существующий комментарий")
    @Test
    void shouldCorrectlyDeleteComment() {
        given(commentService.deleteById(2L)).willReturn(Mono.empty());

        webTestClient.delete()
            .uri("/api/v1/comments/{id}", 2L)
            .exchange()
            .expectStatus().isNoContent();
    }
}
