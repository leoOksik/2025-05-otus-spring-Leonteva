package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

@Mapper(
    componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CommentMapper {

    @Mapping(source = "comment.book.id", target = "bookId")
    CommentDto toDto(Comment comment);

    @Mapping(source = "bookId", target = "book.id")
    Comment toEntity(CommentDto commentDto);
}
