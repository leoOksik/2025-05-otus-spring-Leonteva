package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;

public interface BookService {
    Mono<BookResponseDto> findById(Long id);

    Flux<BookResponseDto> findAll();

    Mono<BookResponseDto> insert(Mono<BookRequestDto> bookRequestDto);

    Mono<BookResponseDto> update(Long id, Mono<BookRequestDto> bookRequestDto);

    Mono<Void> deleteById(Long id);
}
