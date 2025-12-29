package ru.otus.hw.service;

import ru.otus.hw.dto.CommentDto;

import java.util.List;

public interface CommentServiceRestClient {

    List<CommentDto> findByBookId(Long bookId);

    void deleteById(Long id);

}
