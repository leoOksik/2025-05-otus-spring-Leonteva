package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Mongo для работы с книгами ")
@DataMongoTest
@ActiveProfiles("test")
class MongoBookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    GenreRepository genreRepository;

    List<Book> books;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        books = createBooks();
    }

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnBookById() {
        var expectedBook = books.get(0);

        var returnedBook = bookRepository.findById(expectedBook.getId()).orElseThrow();

        assertThat(returnedBook).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnBookList() {
        var expectedBooks = books;

        var returnedBooks = bookRepository.findAll();

        assertThat(returnedBooks).usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedBooks);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var author = authorRepository.save(new Author(null, "Author_test"));
        var genre1 = genreRepository.save(new Genre(null, "Genre_test_1"));
        var genre2 = genreRepository.save(new Genre(null, "Genre_test_2"));
        var book = new Book(null, "Title_test", author, new ArrayList<>(List.of(genre1, genre2)));

        var savedBook = bookRepository.save(book);

        var returnedBook = bookRepository.findById(savedBook.getId()).orElseThrow();
        assertThat(returnedBook)
            .satisfies(b -> {
                assertThat(b.getTitle()).isEqualTo("Title_test");
                assertThat(b.getAuthor()).isEqualTo(author);
                assertThat(b.getGenres()).containsExactlyInAnyOrder(genre1, genre2);
            });
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var author = authorRepository.save(new Author(null, "Author_test"));
        var genre1 = genreRepository.save(new Genre(null, "Genre_test_1"));
        var genre2 = genreRepository.save(new Genre(null, "Genre_test_2"));

        var updatedBook = bookRepository.findById(books.get(1).getId()).orElseThrow();
        updatedBook.setTitle("New Title");
        updatedBook.setGenres(new ArrayList<>(List.of(genre1, genre2)));
        updatedBook.setAuthor(author);

        var returnedBook = bookRepository.save(updatedBook);

        assertThat(returnedBook)
            .satisfies(b -> {
                assertThat(b.getTitle()).isEqualTo("New Title");
                assertThat(b.getAuthor()).isEqualTo(author);
                assertThat(b.getGenres()).containsExactlyInAnyOrder(genre1, genre2);
            });
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        var book = bookRepository.findById(books.get(2).getId()).orElseThrow();

        bookRepository.deleteById(book.getId());

        var deletedBook = bookRepository.findById(book.getId());
        assertThat(deletedBook).isNotPresent();
    }

    private List<Book> createBooks() {
        return Stream.of("Title_1", "Title_2", "Title_3")
            .map(title -> bookRepository.save(new Book(null, title, null, new ArrayList<>())))
            .collect(Collectors.toList());
    }
}
