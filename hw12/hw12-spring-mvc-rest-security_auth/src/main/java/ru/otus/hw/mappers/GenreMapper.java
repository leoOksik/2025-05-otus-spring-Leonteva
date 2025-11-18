package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

@Mapper(
    componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface GenreMapper {
    GenreDto toDto(Genre genre);

    Genre toEntity(GenreDto dto);
}
