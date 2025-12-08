package ru.otus.hw.services.resilience4j;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class AuthorServiceImplResilence4jTest {
    private static final Long GET_AUTHOR_ID = 1L;

    @Autowired
    private RateLimiterRegistry registry;

    @Autowired
    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        IntStream.range(0, 10).forEach(
            i -> authorService.insert(new AuthorDto(null, "Author_" + i + 5)));
        resetRateLimiter();
    }

    @AfterEach
    void tearDown() {
        resetRateLimiter();
    }

    @WithMockUser(roles = "ADMIN")
    @ParameterizedTest()
    @MethodSource("rateLimiterMethods")
    @DisplayName("Rate limiter должен блокировать find and insert запросы после превышения лимита")
    void shouldBlockFindAndInsertAfterExceedingLimit(Consumer<AuthorService> method) {
        IntStream.range(0, 10).forEach(i -> method.accept(authorService));
        assertThrows(RequestNotPermitted.class, () -> method.accept(authorService));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("Rate limiter должен блокировать delete запросы после превышения лимита")
    void shouldBlockDeleteAfterExceedingLimit() {
        IntStream.range(5, 15).forEach(i -> authorService.deleteById((long) i));

        assertThrows(RequestNotPermitted.class, () -> authorService.deleteById(15L));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("Rate limiter должен блокировать update запросы после превышения лимита")
    void shouldBlockUpdateAfterExceedingLimit() {
        IntStream.range(5, 15).forEach(i ->
            authorService.update((long) i, new AuthorDto((long) i, "name" + i)));

        assertThrows(RequestNotPermitted.class, () -> authorService.update(15L, new AuthorDto()));
    }

    static Stream<Named<Consumer<AuthorService>>> rateLimiterMethods() {
        return Stream.of(
            Named.of("findById", authorService -> authorService.findById(GET_AUTHOR_ID)),
            Named.of("findAll", AuthorService::findAll),
            Named.of("insert", authorService -> authorService.insert(new AuthorDto(null, "name")))
        );
    }

    void resetRateLimiter() {
        RateLimiterConfig config = registry.rateLimiter("dbAuthorRateLimiter").getRateLimiterConfig();
        registry.replace("dbAuthorRateLimiter", RateLimiter.of("dbAuthorRateLimiter", config));
    }
}
