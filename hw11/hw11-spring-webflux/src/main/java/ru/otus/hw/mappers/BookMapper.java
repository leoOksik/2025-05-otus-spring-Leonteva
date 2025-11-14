package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.models.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toEntity(BookRequestDto dto);
}
