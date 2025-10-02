package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorDto;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<AuthorDto> findAll();

    Optional<AuthorDto> findById(Long id);

    AuthorDto insert(AuthorDto authorDto);

    AuthorDto update(AuthorDto authorDto);

    void deleteById(Long id);
}
