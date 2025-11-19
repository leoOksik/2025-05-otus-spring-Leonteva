package ru.otus.hw.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.model.mongo.CommentMongo;

public interface MongoCommentRepository extends MongoRepository<CommentMongo, String> {
}
