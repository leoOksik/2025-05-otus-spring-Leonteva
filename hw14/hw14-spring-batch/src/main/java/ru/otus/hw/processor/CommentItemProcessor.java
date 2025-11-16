package ru.otus.hw.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.model.jpa.Author;
import ru.otus.hw.model.jpa.Book;
import ru.otus.hw.model.jpa.Comment;
import ru.otus.hw.model.jpa.Genre;
import ru.otus.hw.model.mongo.AuthorMongo;
import ru.otus.hw.model.mongo.BookMongo;
import ru.otus.hw.model.mongo.CommentMongo;
import ru.otus.hw.model.mongo.GenreMongo;
import ru.otus.hw.repository.jpa.JpaBookRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CommentItemProcessor implements StepExecutionListener, ItemProcessor<Comment, CommentMongo> {

    private final JpaBookRepository jpaBookRepository;

    private Map<Long, BookMongo> booksMongo;

    @Override
    public void beforeStep(StepExecution stepExecution) {

        List<Book> books = jpaBookRepository.findAll();

        booksMongo = books.stream().collect(Collectors.toMap(Book::getId,
            book -> new BookMongo(
                book.getId().toString(), book.getTitle(),
                mapAuthor(book.getAuthor()), mapGenres(book.getGenres())
            )
        ));
    }

    @Override
    public CommentMongo process(Comment comment) {

        return new CommentMongo(
            comment.getId().toString(),
            comment.getText(),
            booksMongo.get(comment.getBook().getId())
        );
    }

    private AuthorMongo mapAuthor(Author author) {
        return new AuthorMongo(author.getId().toString(), author.getFullName());
    }

    private List<GenreMongo> mapGenres(List<Genre> genres) {
        return genres.stream()
            .map(g -> new GenreMongo(g.getId().toString(), g.getName()))
            .toList();
    }
}