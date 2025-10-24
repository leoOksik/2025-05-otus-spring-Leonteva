package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto findById(Long id);

    List<CommentDto> findByBookId(Long bookId);

    CommentDto insert(CommentDto commentDto);

    CommentDto update(Long id, CommentDto commentDto);

    void deleteById(Long id);
}
