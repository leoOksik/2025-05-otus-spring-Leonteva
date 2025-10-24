package ru.otus.hw.services;

import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;

import java.util.List;

public interface BookService {
    BookResponseDto findById(Long id);

    List<BookResponseDto> findAll();

    BookResponseDto insert(BookRequestDto bookRequestDto);

    BookResponseDto update(Long id,BookRequestDto bookRequestDto);

    void deleteById(Long id);
}
