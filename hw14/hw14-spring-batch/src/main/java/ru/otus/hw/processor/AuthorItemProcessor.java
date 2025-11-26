package ru.otus.hw.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.model.jpa.Author;
import ru.otus.hw.model.mongo.AuthorMongo;

@RequiredArgsConstructor
public class AuthorItemProcessor implements ItemProcessor<Author, AuthorMongo> {

    @Override
    public AuthorMongo process(Author author) {
        AuthorMongo authorMongo = new AuthorMongo();
        authorMongo.setFullName(author.getFullName());
        return authorMongo;
    }
}
