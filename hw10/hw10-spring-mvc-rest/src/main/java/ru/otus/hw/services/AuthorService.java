package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorDto;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> findAll();

    AuthorDto findById(Long id);

    AuthorDto insert(AuthorDto authorDto);

    AuthorDto update(Long id, AuthorDto authorDto);

    void deleteById(Long id);
}
