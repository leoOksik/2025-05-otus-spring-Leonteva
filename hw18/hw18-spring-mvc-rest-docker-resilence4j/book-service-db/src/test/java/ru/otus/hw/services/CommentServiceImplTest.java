package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.mappers.CommentMapperImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
@Import({CommentServiceImpl.class, CommentMapperImpl.class})
class CommentServiceImplTest {

    private static final Long GET_COMMENT_ID = 1L;

    private static final Long GET_COMMENT_BOOK_ID = 1L;

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("должен загружать комментарий по id")
    void shouldReturnCommentById() {
        var comment = commentService.findById(GET_COMMENT_ID);

        assertDoesNotThrow(() -> {
            assertThat(comment).isNotNull();
            assertThat(comment.getBookId()).isNotNull();
            assertThat(comment.getText()).isNotNull();
        });
    }

    @Test
    @DisplayName("должен загружать комментарии по id книги")
    void shouldReturnCommentsByBookId() {
        var comments = commentService.findByBookId(GET_COMMENT_BOOK_ID);

        assertThat(comments).isNotEmpty();
        comments.forEach(c -> {
            assertThat(c.getBookId()).isEqualTo(GET_COMMENT_BOOK_ID);
            assertThat(c.getText()).isNotNull();
        });
    }
}
