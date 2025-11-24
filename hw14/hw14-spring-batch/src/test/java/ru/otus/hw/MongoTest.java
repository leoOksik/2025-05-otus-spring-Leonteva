package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.model.mongo.AuthorMongo;
import ru.otus.hw.model.mongo.BookMongo;
import ru.otus.hw.model.mongo.CommentMongo;
import ru.otus.hw.model.mongo.GenreMongo;
import ru.otus.hw.repository.mongo.MongoAuthorRepository;
import ru.otus.hw.repository.mongo.MongoBookRepository;
import ru.otus.hw.repository.mongo.MongoCommentRepository;
import ru.otus.hw.repository.mongo.MongoGenreRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
class MongoTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job jobMigration;

    @Autowired
    private MongoBookRepository mongoBookRepository;

    @Autowired
    private MongoAuthorRepository mongoAuthorRepository;

    @Autowired
    private MongoGenreRepository mongoGenreRepository;

    @Autowired
    private MongoCommentRepository mongoCommentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() throws Exception {
        mongoTemplate.getDb().drop();

        jobLauncher.run(
            jobMigration,
            new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()
        );
    }

    @DisplayName("должен вернуть все жанры из MongoDB после миграции")
    @Test
    void shouldReturnGenresFromMongoDBAfterMigration() {
        var genresMongoExpected = getGenres();

        var genresMongoActual = mongoGenreRepository.findAll();

        assertThat(genresMongoActual)
            .hasSize(genresMongoExpected.size())
            .usingRecursiveComparison().ignoringFields("id")
            .isEqualTo(genresMongoExpected);
    }

    @DisplayName("должен вернуть всех авторов из MongoDB после миграции")
    @Test
    void shouldReturnAuthorsFromMongoDBAfterMigration() {
        var authorsMongoExpected = getAuthors();

        var authorsMongoActual = mongoAuthorRepository.findAll();

        assertThat(authorsMongoActual)
            .hasSize(authorsMongoExpected.size())
            .usingRecursiveComparison().ignoringFields("id")
            .isEqualTo(authorsMongoExpected);
    }

    @DisplayName("должен вернуть все книги из MongoDB после миграции")
    @Test
    void shouldReturnBooksFromMongoDBAfterMigration() {
        var booksMongoExpected = getBooks();

        var booksMongoActual = mongoBookRepository.findAll();

        assertThat(booksMongoActual)
            .hasSize(booksMongoExpected.size())
            .usingRecursiveComparison()
            .ignoringFields("id", "author.id", "genres.id")
            .isEqualTo(booksMongoExpected);
    }

    @DisplayName("должен вернуть все комменты из MongoDB после миграции")
    @Test
    void shouldReturnCommentsFromMongoDBAfterMigration() {
        var commentsMongoExpected = getComments();

        var commentsMongoActual = mongoCommentRepository.findAll();

        assertThat(commentsMongoActual)
            .hasSize(commentsMongoExpected.size())
            .usingRecursiveComparison()
            .ignoringFields("id", "book.id", "book.author.id",  "book.genres.id")
            .isEqualTo(commentsMongoExpected);
    }

    private List<AuthorMongo> getAuthors() {
        return new ArrayList<>(
            List.of(
                new AuthorMongo("1", "Author_1"),
                new AuthorMongo("2", "Author_2"),
                new AuthorMongo("3", "Author_3")
            )
        );
    }

    private List<GenreMongo> getGenres() {
        return new ArrayList<>(
            List.of(
                new GenreMongo("1", "Genre_1"),
                new GenreMongo("2", "Genre_2"),
                new GenreMongo("3", "Genre_3"),
                new GenreMongo("4", "Genre_4"),
                new GenreMongo("5", "Genre_5"),
                new GenreMongo("6", "Genre_6")
            )
        );
    }

    private List<BookMongo> getBooks() {
        var authorsMongo = getAuthors();
        var genresMongo = getGenres();

        return new ArrayList<>(
            List.of(
                new BookMongo("1", "BookTitle_1",
                    authorsMongo.get(0),
                    List.of(genresMongo.get(0), genresMongo.get(1))),
                new BookMongo("2", "BookTitle_2",
                    authorsMongo.get(1),
                    List.of(genresMongo.get(2), genresMongo.get(3))),
                new BookMongo("3", "BookTitle_3",
                    authorsMongo.get(2),
                    List.of(genresMongo.get(4), genresMongo.get(5)))
            )
        );
    }

    private List<CommentMongo> getComments() {
        var booksMongo = getBooks();
        return new ArrayList<>(
            List.of(
                new CommentMongo("1", "Comment_1_1", booksMongo.get(0)),
                new CommentMongo("2", "Comment_1_2", booksMongo.get(0)),
                new CommentMongo("3", "Comment_2_1", booksMongo.get(1)),
                new CommentMongo("4", "Comment_3_1", booksMongo.get(2))
            )
        );
    }
}