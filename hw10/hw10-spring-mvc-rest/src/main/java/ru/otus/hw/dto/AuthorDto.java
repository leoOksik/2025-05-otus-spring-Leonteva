package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.services.ValidationId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDto {

    @Null(groups = ValidationId.OnCreate.class)
    @NotNull(groups = ValidationId.OnUpdate.class)
    private Long id;

    @NotBlank(message = "Author name should not be blank")
    @Size(max = 100, message = "Author name should be less than {max} characters")
    private String fullName;
}
