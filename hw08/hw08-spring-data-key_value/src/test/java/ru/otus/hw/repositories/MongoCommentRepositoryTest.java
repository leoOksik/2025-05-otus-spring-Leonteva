package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Mongo для работы с комментариями")
@DataMongoTest
@ActiveProfiles("test")
public class MongoCommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    BookRepository bookRepository;

    Book book;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        bookRepository.deleteAll();
        book = createBook();
    }

    @Test
    @DisplayName("должен загружать комментарий по id")
    void shouldReturnCommentById() {
        var expectedComment = commentRepository.save(new Comment(null, "Comment_1", null));

        var actualComment = commentRepository.findById(expectedComment.getId());

        assertThat(actualComment).isPresent().get().usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("должен загружать комментарии по id книги")
    void shouldReturnCommentByBookId() {
        var expectedComment = commentRepository.save(new Comment(null, "Comment_1", book));
        var expectedComment2 = commentRepository.save(new Comment(null, "Comment_2", book));
        var expectedComments = List.of(expectedComment, expectedComment2);

        var returnedComments = commentRepository.findByBookId(book.getId());

        assertThat(returnedComments).usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedComments);
    }

    @Test
    @DisplayName("должен сохранять комментарий")
    void shouldSaveComment() {
        var comment = new Comment(null, "Comment_test", book);

        var savedComment = commentRepository.save(comment);

        var returnedComment = commentRepository.findById(savedComment.getId()).orElseThrow();
        assertThat(returnedComment)
            .satisfies(newComment -> {
                assertThat(newComment.getText()).isEqualTo("Comment_test");
                assertThat(newComment.getBook()).isEqualTo(book);
            });
    }

    @Test
    @DisplayName("должен обновлять комментарий")
    void shouldUpdateComment() {
        var comment = commentRepository.save(new Comment(null, "Comment_test", book));
        var savedComment = commentRepository.findById(comment.getId()).orElseThrow();

        savedComment.setText("New_comment_test");
        commentRepository.save(savedComment);

        var updatedComment = commentRepository.findById(savedComment.getId()).orElseThrow();
        assertThat(updatedComment.getText()).isEqualTo("New_comment_test");
    }

    @Test
    @DisplayName("должен удалять комментарий")
    void shouldDeleteCommentById() {
        var savedComment = commentRepository.save(new Comment(null, "Comment_test", book));
        var returnedComment = commentRepository.findById(savedComment.getId()).orElseThrow();

        commentRepository.deleteById(returnedComment.getId());

        var deletedComment = commentRepository.findById(returnedComment.getId());
        assertThat(deletedComment).isNotPresent();
    }

    private Book createBook() {
        return bookRepository.save(new Book(null, "Title_1", null, new ArrayList<>()));
    }
}
