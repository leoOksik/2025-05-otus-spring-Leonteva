package ru.otus.hw.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("books")
public class Book {

    @Id
    @NotNull
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Author author;

    private List<Genre> genres;
}
