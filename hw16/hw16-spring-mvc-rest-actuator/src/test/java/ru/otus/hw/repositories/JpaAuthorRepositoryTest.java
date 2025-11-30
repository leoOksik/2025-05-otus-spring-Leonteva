package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с авторами")
@DataJpaTest
public class JpaAuthorRepositoryTest {

    private static final Long GET_AUTHOR_ID = 1L;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("должен загружать автора по id")
    void shouldReturnCorrectAuthorById() {
        var expectedAuthor = testEntityManager.find(Author.class, GET_AUTHOR_ID);

        var actualAuthor = authorRepository.findById(GET_AUTHOR_ID);

        assertThat(actualAuthor).isPresent().get().usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("должен загружать список всех авторов")
    void shouldReturnCorrectAuthorList() {
        var returnedAuthors = authorRepository.findAll();

        assertThat(returnedAuthors).isNotNull().hasSize(4)
            .allMatch(a -> a.getFullName() != null && !a.getFullName().isBlank());
    }
}
