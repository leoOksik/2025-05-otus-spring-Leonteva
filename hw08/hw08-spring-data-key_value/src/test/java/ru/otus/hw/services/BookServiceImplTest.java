package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.events.MongoBookCascadeDeleteEventsListener;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с книгами на основе MongoDB репозитория")
@DataMongoTest
@Import({BookServiceImpl.class, MongoBookCascadeDeleteEventsListener.class})
@ActiveProfiles("test")
public class BookServiceImplTest {

    @Autowired
    BookServiceImpl bookService;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    CommentRepository commentRepository;

    List<Book> books;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        commentRepository.deleteAll();
        books = createBooks();
    }

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnBookById() {
        var expectedBook = books.get(0);

        var returnedBook = bookService.findById(expectedBook.getId()).orElseThrow();

        assertThat(returnedBook).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnBookList() {
        var expectedBooks = books;

        var returnedBooks = bookService.findAll();

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

        var savedBook = bookService.insert(book.getTitle(),book.getAuthor().getId(),
            new HashSet<>(Set.of(genre1.getId(),genre2.getId())));

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

        var book = bookRepository.findById(books.get(1).getId()).orElseThrow();
        book.setTitle("New Title");
        book.setGenres(new ArrayList<>(List.of(genre1, genre2)));
        book.setAuthor(author);

        var updatedBook = bookService.update(book.getId(),book.getTitle(),
            book.getAuthor().getId(), new HashSet<>(Set.of(genre1.getId(),genre2.getId())));

        var returnedBook = bookRepository.findById(updatedBook.getId()).orElseThrow();

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
        var comment1 = new Comment(null, "Comment_1", book);
        var comment2 = new Comment(null, "Comment_2", book);
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        bookService.deleteById(book.getId());

        var deletedBook = bookRepository.findById(book.getId());
        var deletedComments = commentRepository.findByBookId(book.getId());

        assertThat(deletedBook).isNotPresent();
        assertThat(deletedComments).isEmpty();
    }

    private List<Book> createBooks() {
        return Stream.of("Title_1", "Title_2", "Title_3")
            .map(title -> bookRepository.save(new Book(null, title, null, new ArrayList<>())))
            .collect(Collectors.toList());
    }
}
