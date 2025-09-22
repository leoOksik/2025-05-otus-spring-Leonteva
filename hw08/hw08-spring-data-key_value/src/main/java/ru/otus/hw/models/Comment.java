package ru.otus.hw.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection =  "comments")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {

    @Id
    private String id;

    @Field(name = "text")
    private String text;

    @DocumentReference
    private Book book;

}
