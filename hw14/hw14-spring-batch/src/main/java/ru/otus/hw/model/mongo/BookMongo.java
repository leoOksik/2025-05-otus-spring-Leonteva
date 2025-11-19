package ru.otus.hw.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "books")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookMongo {

    @Id
    private String id;

    @Field(name = "title")
    private String title;

    @DocumentReference
    @Field("author")
    private AuthorMongo author;

    @DocumentReference
    @Field("genres")
    private List<GenreMongo> genres;
}
