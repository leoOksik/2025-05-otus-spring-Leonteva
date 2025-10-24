package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.mappers.AuthorMapperImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
@Import({AuthorServiceImpl.class, AuthorMapperImpl.class})
public class AuthorServiceImplTest {
    private static final Long GET_AUTHOR_ID = 1L;

    @Autowired
    private AuthorService authorService;

    @Test
    @DisplayName("должен загружать автора по id")
    void shouldReturnAuthorById() {
        var author = authorService.findById(GET_AUTHOR_ID);

        assertDoesNotThrow(() -> {
            assertThat(author).isNotNull();
            assertThat(author.getFullName()).isEqualTo("Author_1");
        });
    }

    @Test
    @DisplayName("должен загружать всех авторов")
    void shouldReturnCommentByBookIdWithoutLazyException() {
        var authors = authorService.findAll();

        assertThat(authors)
            .isNotNull().hasSize(3)
            .extracting("fullName")
            .containsExactlyInAnyOrder(
                "Author_1",
                "Author_2",
                "Author_3"
            );
    }
}
