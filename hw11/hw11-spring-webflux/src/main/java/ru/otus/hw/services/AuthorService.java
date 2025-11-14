package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;

public interface AuthorService {
    Flux<AuthorDto> findAll();

    Mono<AuthorDto> findById(Long id);

    Mono<AuthorDto> insert(Mono<AuthorDto> authorDto);

    Mono<AuthorDto> update(Long id,  Mono<AuthorDto> authorDto);

    Mono<Void> deleteById(Long id);
}
