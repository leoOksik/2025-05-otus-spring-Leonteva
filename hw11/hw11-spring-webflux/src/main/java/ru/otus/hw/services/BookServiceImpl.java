package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.models.BookGenre;
import ru.otus.hw.repositories.BookGenreRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.BookRepositoryCustom;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookRepositoryCustom bookRepositoryCustom;

    private final BookGenreRepository bookGenreRepository;

    private final BookMapper bookMapper;

    @Override
    public Mono<BookResponseDto> findById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return bookRepositoryCustom.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("Comment not found")));
    }

    @Override
    public Flux<BookResponseDto> findAll() {
        return bookRepositoryCustom.findAll();
    }

    @Transactional
    @Override
    public Mono<BookResponseDto> insert(Mono<BookRequestDto> bookRequestDto) {
        return bookRequestDto.flatMap(dto -> save(null, dto));
    }

    @Transactional
    @Override
    public Mono<BookResponseDto> update(Long id, Mono<BookRequestDto> bookRequestDto) {
        return bookRequestDto.flatMap(dto -> save(id, dto));
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(Long id) {
        return checkExistingBook(id).then(bookRepository.deleteById(id));
    }

    private Mono<Void> checkExistingBook(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return bookRepository.existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new NotFoundException("Book not found"));
                }
                return Mono.empty();
            });
    }

    private Mono<BookResponseDto> save(Long id, BookRequestDto bookRequestDto) {
        Objects.requireNonNull(bookRequestDto, "BookRequestDto must not be null");

        var genreIds = bookRequestDto.getGenreIds();
        if (genreIds == null || genreIds.isEmpty()) {
            return Mono.error(new NotFoundException("Empty genres list"));
        }

        var book = bookMapper.toEntity(bookRequestDto);
        book.setId(id);

        return bookRepository.save(book)
            .flatMap(savedBook ->
                bookGenreRepository.deleteByBookId(savedBook.getId())
                    .thenMany(Flux.fromIterable(genreIds)
                        .map(genreId -> new BookGenre(null, savedBook.getId(), genreId))
                        .as(bookGenreRepository::saveAll))
                    .then(Mono.just(savedBook))
            )
            .flatMap(savedBook -> findById(savedBook.getId()));
    }
}
