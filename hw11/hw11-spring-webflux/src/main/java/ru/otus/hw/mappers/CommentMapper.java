package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

@Mapper(
    componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CommentMapper {

    CommentDto toDto(Comment comment);

    Comment toEntity(CommentDto commentDto);
}
