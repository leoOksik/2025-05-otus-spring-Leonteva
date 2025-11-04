package ru.otus.hw.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table("comments")
public class Comment {

    @Id
    private Long id;

    @Column("text")
    private String text;

    @NotNull
    @Column("book_id")
    private Long bookId;

    @Override
    public String toString() {
        return "Comment{" +
            "id=" + id +
            ", text='" + text + '\'' +
            '}';
    }
}
