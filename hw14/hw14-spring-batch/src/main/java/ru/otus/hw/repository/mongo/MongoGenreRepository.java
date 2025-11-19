package ru.otus.hw.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.model.mongo.GenreMongo;

public interface MongoGenreRepository extends MongoRepository<GenreMongo, String> {
}
