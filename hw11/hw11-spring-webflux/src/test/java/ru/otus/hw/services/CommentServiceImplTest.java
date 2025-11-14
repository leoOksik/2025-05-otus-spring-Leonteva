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
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.CommentRepository;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class CommentServiceImplTest {

    private static final Long GET_COMMENT_ID = 1L;

    private static final Long GET_COMMENT_BOOK_ID = 1L;

    @MockBean
    private CommentRepository commentRepository;

    @Autowired
    private CommentServiceImpl commentService;

    private Flux<Comment> comments;

    @BeforeEach
    void setUp() {
        comments = Flux.just(
            new Comment(1L, "Comment_1", 1L),
            new Comment(2L, "Comment_2", 1L),
            new Comment(2L, "Comment_3", 1L)
        );
    }

    @Test
    @DisplayName("должен загружать комментарий по id")
    void shouldReturnCommentById() {

        Comment comment = new Comment(1L, "Comment_1", 1L);
        when(commentRepository.findById(GET_COMMENT_ID)).thenReturn(Mono.just(comment));

        Mono<CommentDto> result = commentService.findById(GET_COMMENT_ID);

        StepVerifier.create(result)
            .assertNext(dto -> {
                assertThat(dto.getText()).isEqualTo("Comment_1");
                assertThat(dto.getBookId()).isNotNull();
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("должен загружать комментарии по id книги")
    void shouldReturnCommentsByBookId() {
        when(commentRepository.findByBookId(GET_COMMENT_BOOK_ID)).thenReturn((comments));

        Flux<CommentDto> result = commentService.findByBookId(GET_COMMENT_BOOK_ID);

        StepVerifier.create(result)
            .recordWith(ArrayList::new)
            .expectNextCount(3)
            .consumeRecordedWith(comments ->
                assertThat(comments).hasSize(3).allSatisfy(c -> {
                assertThat(c.getBookId()).isEqualTo(GET_COMMENT_BOOK_ID);
                assertThat(c.getText()).isNotEmpty();
            }))
            .verifyComplete();
    }
}
