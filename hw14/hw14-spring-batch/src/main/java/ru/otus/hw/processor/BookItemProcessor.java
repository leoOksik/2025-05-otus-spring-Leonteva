package ru.otus.hw.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.model.jpa.Book;
import ru.otus.hw.model.mongo.AuthorMongo;
import ru.otus.hw.model.mongo.BookMongo;
import ru.otus.hw.model.mongo.GenreMongo;

import java.util.List;


@RequiredArgsConstructor
public class BookItemProcessor implements ItemProcessor<Book, BookMongo> {

    @Override
    public BookMongo process(Book book) {

        AuthorMongo authorMongo = new AuthorMongo(
            book.getAuthor().getId().toString(),
            book.getAuthor().getFullName()
        );

        List<GenreMongo> genresMongo = book.getGenres().stream()
            .map(g -> new GenreMongo(g.getId().toString(), g.getName()))
            .toList();

        return new BookMongo(
            book.getId().toString(),
            book.getTitle(),
            authorMongo,
            genresMongo
        );
    }
}
