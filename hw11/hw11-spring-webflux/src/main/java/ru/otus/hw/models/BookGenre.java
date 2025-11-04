package ru.otus.hw.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table("books_genres")
public class BookGenre {

    @Id
    private Long id;

    @NotNull
    @Column("book_id")
    private Long bookId;

    @NotNull
    @Column("genre_id")
    private Long genreId;

}
