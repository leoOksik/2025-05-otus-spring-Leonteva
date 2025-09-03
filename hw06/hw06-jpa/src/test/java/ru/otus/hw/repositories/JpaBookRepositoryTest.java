package ru.otus.hw.repositories;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе ORM для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class, JpaGenreRepository.class})
@Slf4j
class JpaBookRepositoryTest {

    private static final Long GET_BOOK_ID = 1L;
    private static final Long UPDATED_BOOK_ID = 2L;
    private static final Long DELETED_BOOK_ID = 3L;

    @Autowired
    JpaBookRepository jpaBookRepository;

    @Autowired
    TestEntityManager testEntityManager;


    @DisplayName("должен загружать книгу по id")
    @Test
    @Transactional
    void shouldReturnCorrectBookById() {
        var expectedBook = testEntityManager.find(Book.class, GET_BOOK_ID);

        var actualBook = jpaBookRepository.findById(GET_BOOK_ID);

        assertThat(actualBook).isPresent().get().usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var returnedBooks = jpaBookRepository.findAll();

        assertThat(returnedBooks).isNotNull().hasSize(3)
            .allMatch(b -> !b.getTitle().equals(""))
            .allMatch(b -> b.getAuthor() != null)
            .allMatch(b -> b.getGenres() != null && b.getGenres().size() > 0)
            .anyMatch(b -> b.getComments() != null && b.getComments().size() > 0);

        returnedBooks.forEach(book -> log.info("Books: {}", book));
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var author = new Author(null, "Author_test");
        testEntityManager.persist(author);
        var genre1 = new Genre(null, "Genre_test_1");
        var genre2 = new Genre(null, "Genre_test_2");
        testEntityManager.persist(genre1);
        testEntityManager.persist(genre2);
        var book = new Book(null, "Title_test", author,
            new ArrayList<>(List.of(genre1, genre2)), new ArrayList<>());

        var savedBook = jpaBookRepository.save(book);
        var expectedBook = testEntityManager.find(Book.class, savedBook.getId());

        assertThat(savedBook)
            .isNotNull()
            .matches(b -> b.getId() != null)
            .matches(b -> b.getTitle() != null && b.getTitle().equals("Title_test"))
            .matches(b -> b.getAuthor() != null && b.getAuthor().getFullName().equals("Author_test"))
            .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook).isNotNull();

        log.info("Saved book: {}", savedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var updatedBook = testEntityManager.find(Book.class, UPDATED_BOOK_ID);
        assertThat(updatedBook).isNotNull();
        updatedBook.setTitle("New Title");
        var returnedBook = jpaBookRepository.save(updatedBook);

        var expectedBook = testEntityManager.find(Book.class, updatedBook.getId());

        assertThat(returnedBook).isNotNull()
            .matches(b -> b.getId() > 0)
            .matches(b -> b.getTitle().equals("New Title"))
            .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook).isNotNull();

        log.info("Updated book: {}", returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        var book = testEntityManager.find(Book.class, DELETED_BOOK_ID);

        assertThat(book).isNotNull();

        testEntityManager.detach(book);
        jpaBookRepository.deleteById(DELETED_BOOK_ID);

        var deletedBook = testEntityManager.find(Book.class, DELETED_BOOK_ID);
        assertThat(deletedBook).isNull();
    }
}
