package ru.otus.hw.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.model.mongo.AuthorMongo;

public interface MongoAuthorRepository extends MongoRepository<AuthorMongo, String> {
}
