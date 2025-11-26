package ru.otus.hw.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.model.mongo.BookMongo;

public interface MongoBookRepository extends MongoRepository<BookMongo, String> {
}
