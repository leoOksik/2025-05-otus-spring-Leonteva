package ru.otus.hw.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "authors")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthorMongo {

    @Id
    private String id;

    @Field(name = "full_name")
    private String fullName;
}
