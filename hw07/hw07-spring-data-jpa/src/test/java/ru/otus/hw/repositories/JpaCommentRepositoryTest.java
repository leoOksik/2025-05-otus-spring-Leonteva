package ru.otus.hw.repositories;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с комментариями")
@DataJpaTest
@Slf4j
public class JpaCommentRepositoryTest {

    private static final Long GET_COMMENT_ID = 1L;
    private static final Long GET_COMMENT_BOOK_ID = 1L;
    private static final Long UPDATED_COMMENT_ID = 2L;
    private static final Long DELETED_COMMENT_ID = 3L;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    @DisplayName("должен загружать комментарий по id")
    void shouldReturnCorrectCommentById() {
        var expectedComment = testEntityManager.find(Comment.class, GET_COMMENT_ID);

        var actualComment = commentRepository.findById(GET_COMMENT_ID);

        assertThat(actualComment).isPresent().get().usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("должен загружать комментарии по id книги")
    void shouldReturnCorrectCommentByBookId() {
        var returnedComments = commentRepository.findByBookId(GET_COMMENT_BOOK_ID);

        assertThat(returnedComments).isNotNull().hasSize(2)
            .allMatch(c -> c.getText() != null && !c.getText().isBlank())
            .allMatch(c -> List.of("Comment_1_1", "Comment_1_2").contains(c.getText()))
            .allMatch(c -> c.getBook() != null);

        returnedComments.forEach(comment -> log.info("Comments: {}", comment));
    }

    @Test
    @DisplayName("должен сохранять комментарий")
    void shouldCorrectSaveComment() {
        var book = testEntityManager.find(Book.class, GET_COMMENT_BOOK_ID);
        assertThat(book).isNotNull();
        var comment = new Comment(null, "text_comment_new", book);

        var savedComment = commentRepository.save(comment);
        var expectedComment = testEntityManager.find(Comment.class, savedComment.getId());

        assertThat(savedComment)
            .isNotNull()
            .matches(c -> c.getId() != null)
            .matches(c -> c.getText() != null && c.getText().equals("text_comment_new"))
            .matches(c -> c.getBook() != null)
            .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment).isNotNull();

        log.info("Saved comment: {}", savedComment);
    }

    @Test
    @DisplayName("должен обновлять комментарий")
    void shouldCorrectUpdateComment() {
        var updatedComment = testEntityManager.find(Comment.class, UPDATED_COMMENT_ID);
        assertThat(updatedComment).isNotNull();
        updatedComment.setText("New comment");
        var returnedComment = commentRepository.save(updatedComment);

        var expectedComment = testEntityManager.find(Comment.class, updatedComment.getId());

        assertThat(returnedComment).isNotNull()
            .matches(b -> b.getId() > 0)
            .matches(b -> b.getText().equals("New comment"))
            .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment).isNotNull();

        log.info("Updated comment: {}", returnedComment);
    }

    @Test
    @DisplayName("должен удалять комментарий")
    void shouldCorrectDeleteCommentById() {
        var comment = testEntityManager.find(Comment.class, DELETED_COMMENT_ID);

        assertThat(comment).isNotNull();

        testEntityManager.detach(comment);
        commentRepository.deleteById(DELETED_COMMENT_ID);

        var deletedComment = testEntityManager.find(Comment.class, DELETED_COMMENT_ID);
        assertThat(deletedComment).isNull();
    }
}
