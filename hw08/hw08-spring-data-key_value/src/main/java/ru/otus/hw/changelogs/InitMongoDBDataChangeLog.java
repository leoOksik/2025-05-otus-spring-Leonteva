package ru.otus.hw.changelogs;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.Comment;

import java.util.List;

@RequiredArgsConstructor
@ChangeUnit(id = "initDB", order = "001", author = "oksana", systemVersion = "1")
public class InitMongoDBDataChangeLog {

    private final MongoTemplate mongoTemplate;

    @Execution
    public void migration() {
        var author1 = mongoTemplate.save(new Author(null, "Author_1"));
        var author2 = mongoTemplate.save(new Author(null, "Author_2"));
        var author3 = mongoTemplate.save(new Author(null, "Author_3"));
        var genre1 = mongoTemplate.save(new Genre(null, "Genre_1"));
        var genre2 = mongoTemplate.save(new Genre(null, "Genre_2"));
        var genre3 = mongoTemplate.save(new Genre(null, "Genre_3"));
        var genre4 = mongoTemplate.save(new Genre(null, "Genre_4"));
        var genre5 = mongoTemplate.save(new Genre(null, "Genre_5"));
        var genre6 = mongoTemplate.save(new Genre(null, "Genre_6"));
        var book1 = mongoTemplate.save(new Book(null, "BookTitle_1", author1, List.of(genre1, genre2)));
        var book2 = mongoTemplate.save(new Book(null, "BookTitle_2", author2, List.of(genre3, genre4)));
        var book3 = mongoTemplate.save(new Book(null, "BookTitle_3", author3, List.of(genre5, genre6)));

        mongoTemplate.save(new Comment(null, "Comment_1_1", book1));
        mongoTemplate.save(new Comment(null, "Comment_1_2", book1));
        mongoTemplate.save(new Comment(null, "Comment_2_1", book2));
        mongoTemplate.save(new Comment(null, "Comment_3_1", book3));
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.getDb().drop();
    }
}
