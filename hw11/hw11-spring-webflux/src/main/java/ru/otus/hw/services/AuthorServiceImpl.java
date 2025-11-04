package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @Override
    public Flux<AuthorDto> findAll() {
        return authorRepository.findAll().map(authorMapper::toDto);
    }

    @Override
    public Mono<AuthorDto> findById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return authorRepository.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("Author not found")))
            .map(authorMapper::toDto);
    }

    @Transactional
    @Override
    public Mono<AuthorDto> insert(Mono<AuthorDto> authorDto) {
        return authorDto.map(authorMapper::toEntity)
            .flatMap(authorRepository::save).map(authorMapper::toDto);
    }

    @Transactional
    @Override
    public Mono<AuthorDto> update(Long id, Mono<AuthorDto> authorDto) {
        return checkExistingAuthor(id).then(authorDto)
            .map(authorMapper::toEntity)
            .doOnNext(author -> author.setId(id))
            .flatMap(authorRepository::save).map(authorMapper::toDto);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(Long id) {
        return checkExistingAuthor(id)
            .then(authorRepository.deleteById(id));
    }

    private Mono<Void> checkExistingAuthor(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return authorRepository.existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new NotFoundException("Author not found"));
                }
                return Mono.empty();
            });
    }
}
