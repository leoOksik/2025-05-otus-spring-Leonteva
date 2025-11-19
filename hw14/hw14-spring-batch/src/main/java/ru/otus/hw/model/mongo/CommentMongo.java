package ru.otus.hw.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection =  "comments")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentMongo {

    @Id
    private String id;

    @Field(name = "text")
    private String text;

    @DocumentReference
    @Field(name = "book")
    private BookMongo book;

}
