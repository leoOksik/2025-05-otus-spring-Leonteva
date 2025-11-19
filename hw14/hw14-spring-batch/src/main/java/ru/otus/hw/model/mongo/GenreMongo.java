package ru.otus.hw.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "genres")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GenreMongo {

    @Id
    private String id;

    @Field(name = "name")
    private String name;
}
