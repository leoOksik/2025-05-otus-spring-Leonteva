package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    public Optional<BookResponseDto> findById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return bookRepository.findById(id).map(bookMapper::toResponseDto);
    }

    @Override
    public List<BookResponseDto> findAll() {
        return bookRepository.findAll().stream().map(bookMapper::toResponseDto).toList();
    }

    @Transactional
    @Override
    public BookResponseDto insert(BookRequestDto bookRequestDto) {
        Book book = bookMapper.toEntity(bookRequestDto);
        bookRepository.save(book);
        return bookMapper.toResponseDto(book);
    }

    @Transactional
    @Override
    public BookResponseDto update(BookRequestDto bookRequestDto) {
        if (!bookRepository.existsById(bookRequestDto.getId())) {
            throw new NotFoundException("Book not found");
        }
        Book book = bookMapper.toEntity(bookRequestDto);
        bookRepository.save(book);
        return bookMapper.toResponseDto(book);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        if (!bookRepository.existsById(id)) {
            throw new NotFoundException("Book not found");
        }
        bookRepository.deleteById(id);
    }
}
