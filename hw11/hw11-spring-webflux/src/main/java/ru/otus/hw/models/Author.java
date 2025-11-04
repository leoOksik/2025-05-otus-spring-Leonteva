package ru.otus.hw.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table("authors")
public class Author {

    @Id
    private Long id;

    @NotNull
    @Column("full_name")
    private String fullName;
}
