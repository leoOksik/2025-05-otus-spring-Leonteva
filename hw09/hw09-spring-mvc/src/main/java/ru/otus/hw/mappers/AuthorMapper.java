package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

@Mapper(
    componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AuthorMapper {

    AuthorDto toDto(Author author);

    Author toEntity(AuthorDto dto);
}
