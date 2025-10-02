package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    @NotBlank(message = "Title should not be blank")
    @Size(max = 30, message = "Title should be less than {max} characters")
    private String title;

    private AuthorDto author;

    private List<GenreDto> genres;
}

