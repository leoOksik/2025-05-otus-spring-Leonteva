package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto {
    private Long id;

    private String title;

    private AuthorDto author;

    private List<GenreDto> genres;
}
