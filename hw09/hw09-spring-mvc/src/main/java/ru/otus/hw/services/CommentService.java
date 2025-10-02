package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<CommentDto> findById(Long id);

    List<CommentDto> findByBookId(Long id);

    CommentDto insert(CommentDto commentDto);

    CommentDto update(CommentDto commentDto);

    void deleteById(Long id);
}
