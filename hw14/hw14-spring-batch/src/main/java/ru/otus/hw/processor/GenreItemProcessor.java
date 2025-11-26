package ru.otus.hw.processor;

import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.model.jpa.Genre;
import ru.otus.hw.model.mongo.GenreMongo;

public class GenreItemProcessor implements ItemProcessor<Genre, GenreMongo> {

    @Override
    public GenreMongo process(Genre genre) {
        GenreMongo genreMongo = new GenreMongo();
        genreMongo.setName(genre.getName());
       return genreMongo;
    }
}