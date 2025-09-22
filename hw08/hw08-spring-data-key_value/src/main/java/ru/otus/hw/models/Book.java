package ru.otus.hw.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "books")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Book {

    @Id
    private String id;

    @Field(name = "title")
    private String title;

    @DocumentReference
    @Field("authorId")
    private Author author;

    @DocumentReference
    private List<Genre> genres;
}
