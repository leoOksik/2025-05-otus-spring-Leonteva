package ru.otus.hw.services.resilience4j;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BookServiceImplResilence4jTest {

    private static final Long GET_BOOK_ID = 1L;

    @Autowired
    private RateLimiterRegistry registry;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        Author author = authorRepository.save(new Author(null, "Test Author"));
        IntStream.range(0, 5).forEach(i ->
            bookRepository.save(
                new Book(null, "titleNew" + i, author, List.of())
            )
        );

        resetRateLimiter();
    }

    @AfterEach
    void tearDown() {
        resetRateLimiter();
    }

    @Transactional(readOnly = true)
    @WithMockUser(roles = "ADMIN")
    @ParameterizedTest()
    @MethodSource("rateLimiterMethods")
    @DisplayName("Rate limiter должен блокировать find запросы после превышения лимита")
    void shouldBlockFindAfterExceedingLimit(Consumer<BookService> method) {
        IntStream.range(0, 5).forEach(i -> method.accept(bookService));
        assertThrows(RequestNotPermitted.class, () -> method.accept(bookService));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("Rate limiter должен блокировать insert запросы после превышения лимита")
    void shouldBlockInsertAfterExceedingLimit() {
        IntStream.range(0, 5).forEach(i ->
            bookService.insert(new BookRequestDto(null, "title" + i, 1L, Set.of())));

        assertThrows(RequestNotPermitted.class, () -> bookService.insert(new BookRequestDto()));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("Rate limiter должен блокировать update запросы после превышения лимита")
    void shouldBlockUpdateAfterExceedingLimit() {
        IntStream.range(4, 9).forEach(i ->
            bookService.update((long) i, new BookRequestDto((long) i, "title" + i, 1L, Set.of())));

        assertThrows(RequestNotPermitted.class, () ->
            bookService.update(9L, new BookRequestDto(9L, "title", 1L, Set.of())));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("Rate limiter должен блокировать delete запросы после превышения лимита")
    void shouldBlockDeleteAfterExceedingLimit() {
        IntStream.range(4, 9).forEach(i -> bookService.deleteById((long) i));

        assertThrows(RequestNotPermitted.class, () -> bookService.deleteById(10L));
    }

    static Stream<Named<Consumer<BookService>>> rateLimiterMethods() {
        return Stream.of(
            Named.of("findById", bookService -> bookService.findById(GET_BOOK_ID)),
            Named.of("findAll", BookService::findAll)
        );
    }

    void resetRateLimiter() {
        RateLimiterConfig config = registry.rateLimiter("dbBookRateLimiter").getRateLimiterConfig();
        registry.replace("dbBookRateLimiter", RateLimiter.of("dbBookRateLimiter", config));
    }
}
