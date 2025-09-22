package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с авторами на основе MongoDB репозитория")
@DataMongoTest
@Import(AuthorServiceImpl.class)
@ActiveProfiles("test")
public class AuthorServiceImplTest {

    @Autowired
    AuthorServiceImpl authorService;

    @Autowired
    AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
    }

    @Test
    @DisplayName("должен загружать автора по id ")
    void shouldReturnAuthorById() {
        assertThat(authorRepository.count()).isEqualTo(0);

        var expectedAuthor = authorRepository.save(new Author(null, "Author_1"));

        var actualAuthor = authorService.findById(expectedAuthor.getId()).orElseThrow();

        assertThat(actualAuthor).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("должен загружать список всех авторов")
    void shouldReturnCorrectAuthorList() {
        var author = authorRepository.save(new Author(null, "Author_1_test"));
        var author2 = authorRepository.save(new Author(null, "Author_2_test"));
        var author3 = authorRepository.save(new Author(null, "Author_3_test"));
        var expectedAuthors = Arrays.asList(author, author2, author3);

        var returnedAuthors = authorService.findAll();

        assertThat(returnedAuthors).usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedAuthors);
    }
}
