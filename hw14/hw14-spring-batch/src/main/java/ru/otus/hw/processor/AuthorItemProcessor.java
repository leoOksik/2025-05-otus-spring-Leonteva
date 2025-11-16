package ru.otus.hw.processor;

import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.model.jpa.Author;
import ru.otus.hw.model.mongo.AuthorMongo;

public class AuthorItemProcessor implements ItemProcessor<Author, AuthorMongo> {

    @Override
    public AuthorMongo process(Author author) {
        return new AuthorMongo(
            author.getId().toString(),
            author.getFullName()
        );
    }
}
