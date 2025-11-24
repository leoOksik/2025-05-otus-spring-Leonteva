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

    private final Cache cache;

    @Override
    public BookMongo process(Book book) {

        AuthorMongo authorMongo = new AuthorMongo(
            cache.get("Author", book.getAuthor().getId()),
            book.getAuthor().getFullName());

        List<GenreMongo> genresMongo = book.getGenres().stream()
            .map(g -> new GenreMongo(
                cache.get("Genre", g.getId()),
                g.getName()))
            .toList();

        BookMongo bookMongo = new BookMongo();
        bookMongo.setTitle(book.getTitle());
        bookMongo.setAuthor(authorMongo);
        bookMongo.setGenres(genresMongo);

        return bookMongo;
    }
}
