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
    @DisplayName("должен загружать комментарий по id без LazyInitializationException")
    void shouldReturnCommentByIdWithoutLazyException() {
        var comment = commentService.findById(GET_COMMENT_ID).orElseThrow();

        assertThat(comment).isNotNull();
        assertDoesNotThrow(() -> {
            var book = comment.getBook();
            assertThat(book).isNotNull();
            assertThat(book.getAuthor()).isNotNull();
            assertThat(book.getGenres()).isNotEmpty();
        });
    }

    @Test
    @DisplayName("должен загружать комментарии по id книги без LazyInitializationException")
    void shouldReturnCommentsByBookIdWithoutLazyException() {
        var comments = commentService.findByBookId(GET_COMMENT_BOOK_ID);

        assertDoesNotThrow(() -> {
            assertThat(comments).isNotNull().isNotEmpty();
            comments.forEach(comment -> {
                var book = comment.getBook();
                assertThat(book).isNotNull();
                assertThat(book.getAuthor()).isNotNull();
                assertThat(book.getGenres()).isNotEmpty();
            });
        });
    }
}
