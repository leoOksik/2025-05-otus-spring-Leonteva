package ru.otus.hw.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDto {
    private Long id;

    @NotBlank(message = "Author name should not be blank")
    @Size(max = 100, message = "Author name should be less than {max} characters")
    private String fullName;
}
