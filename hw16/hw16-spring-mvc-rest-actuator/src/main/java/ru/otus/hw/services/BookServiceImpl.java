package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;


    @Override
    public BookResponseDto findById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return bookRepository.findById(id).map(bookMapper::toResponseDto)
            .orElseThrow(() -> new NotFoundException("Book not found"));
    }

    @Override
    public List<BookResponseDto> findAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
            .map(bookMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public BookResponseDto insert(BookRequestDto bookRequestDto) {
        final Book book = bookMapper.toEntity(bookRequestDto);
        final Book savedBook = bookRepository.save(book);

        return bookMapper.toResponseDto(savedBook);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public BookResponseDto update(Long id, BookRequestDto bookRequestDto) {
        checkExistingBook(id);
        final Book updatedBook = bookMapper.toEntity(bookRequestDto);
        updatedBook.setId(id);
        final Book savedBook = bookRepository.save(updatedBook);
        return bookMapper.toResponseDto(savedBook);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public void deleteById(Long id) {
        checkExistingBook(id);
        bookRepository.deleteById(id);
    }

    private void checkExistingBook(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        if (!bookRepository.existsById(id)) {
            throw new NotFoundException("Author not found");
        }
    }

}
