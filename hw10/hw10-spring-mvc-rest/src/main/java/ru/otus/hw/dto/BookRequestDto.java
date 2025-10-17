package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {

    private Long id;

    @NotBlank(message = "Title should not be blank")
    @Size(max = 30, message = "Title should be less than {max} characters")
    private String title;

    @NotNull(message = "Author must be selected")
    private Long authorId;

    private Set<Long> genreIds;
}
