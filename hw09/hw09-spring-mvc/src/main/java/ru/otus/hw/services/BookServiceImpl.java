package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
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
    public Optional<BookDto> findById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return bookRepository.findById(id).map(bookMapper::toDto);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream().map(bookMapper::toDto).toList();
    }

    @Transactional
    @Override
    public BookDto insert(BookDto bookDto) {
        Book book = bookMapper.toEntity(bookDto);
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Transactional
    @Override
    public BookDto update(BookDto bookDto) {
        if (!bookRepository.existsById(bookDto.getId())) {
            throw new NotFoundException();
        }
        Book book = bookMapper.toEntity(bookDto);
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        if (!bookRepository.existsById(id)) {
            throw new NotFoundException();
        }
        bookRepository.deleteById(id);
    }
}
