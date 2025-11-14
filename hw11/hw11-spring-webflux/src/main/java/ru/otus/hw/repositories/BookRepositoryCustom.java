package ru.otus.hw.repositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookResponseDto;

public interface BookRepositoryCustom {
    Flux<BookResponseDto> findAll();

    Mono<BookResponseDto> findById(Long id);
}
