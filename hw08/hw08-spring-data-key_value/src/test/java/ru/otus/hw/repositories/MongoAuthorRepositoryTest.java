package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Mongo для работы с авторами")
@DataMongoTest
@ActiveProfiles("test")
public class MongoAuthorRepositoryTest {

    @Autowired
    AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
    }

    @Test
    @DisplayName("должен загружать автора по id")
    void shouldReturnAuthorById() {
        var expectedAuthor = authorRepository.save(new Author(null, "Author_1"));

        var actualAuthor = authorRepository.findById(expectedAuthor.getId()).orElseThrow();

        assertThat(actualAuthor).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("должен загружать список всех авторов")
    void shouldReturnAuthorList() {
        var author = authorRepository.save(new Author(null, "Author_1"));
        var author2 = authorRepository.save(new Author(null, "Author_2"));
        var author3 = authorRepository.save(new Author(null, "Author_3"));
        var expectedAuthors = Arrays.asList(author, author2, author3);

        var returnedAuthors = authorRepository.findAll();

        assertThat(returnedAuthors).usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedAuthors);
    }
}
