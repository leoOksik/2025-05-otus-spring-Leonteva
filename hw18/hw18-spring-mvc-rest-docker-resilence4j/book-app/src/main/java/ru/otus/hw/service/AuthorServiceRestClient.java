package ru.otus.hw.service;

import ru.otus.hw.dto.AuthorDto;

import java.util.List;

public interface AuthorServiceRestClient {
    List<AuthorDto> findAll();

    AuthorDto findById(Long id);

    AuthorDto insert(AuthorDto authorDto);

    AuthorDto update(Long id, AuthorDto authorDto);

    void deleteById(Long id);
}
