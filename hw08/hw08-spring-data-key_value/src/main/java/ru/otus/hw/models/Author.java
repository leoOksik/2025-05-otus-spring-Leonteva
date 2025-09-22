package ru.otus.hw.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Id;

@Document(collection = "authors")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Author {

    @Id
    private String id;

    @Field(name = "full_name")
    private String fullName;
}
