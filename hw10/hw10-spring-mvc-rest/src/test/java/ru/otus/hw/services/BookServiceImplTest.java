package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.mappers.BookMapperImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
@Import({BookServiceImpl.class, BookMapperImpl.class})
public class BookServiceImplTest {

    private static final Long GET_BOOK_ID = 1L;

    @Autowired
    private BookServiceImpl bookService;

    @Test
    @DisplayName("должен загружать книгу по id без LazyInitializationException")
    void shouldReturnBookByIdWithoutLazyException() {
        var book = bookService.findById(GET_BOOK_ID);

        assertThat(book).isNotNull();
        assertDoesNotThrow(() -> {
            assertThat(book.getAuthor()).isNotNull();
            assertThat(book.getGenres()).isNotEmpty();
        });
    }

    @Test
    @DisplayName("должен загружать комментарии по id книги без LazyInitializationException")
    void shouldReturnCommentByBookIdWithoutLazyException() {
        var books = bookService.findAll();

        assertThat(books).isNotNull().isNotEmpty();
        assertDoesNotThrow(() ->
            books.forEach(book -> {
                assertThat(book.getAuthor()).isNotNull();
                assertThat(book.getGenres()).isNotEmpty();
            }));
    }
}
