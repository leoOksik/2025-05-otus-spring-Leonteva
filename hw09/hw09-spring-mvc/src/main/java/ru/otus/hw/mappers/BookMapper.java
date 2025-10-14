package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

@Mapper(
    componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface BookMapper {

    @Mapping(source = "author", target = "author")
    @Mapping(source = "genres", target = "genres")
    BookDto toDto(Book book);

    Book toEntity(BookDto dto);
}
