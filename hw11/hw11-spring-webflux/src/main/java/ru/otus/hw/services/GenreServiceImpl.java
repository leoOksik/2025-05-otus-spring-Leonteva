package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mappers.GenreMapper;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @Override
    public Flux<GenreDto> findAll() {
        return genreRepository.findAll().map(genreMapper::toDto);
    }

    @Override
    public Flux<GenreDto> findAllByIdIn(Set<Long> ids) {
        Objects.requireNonNull(ids, "Ids must not be null");

        if (ids.contains(null)) {
            throw  new IllegalArgumentException("Ids set contains null");
        }
        return genreRepository.findAllByIdIn(ids).map(genreMapper::toDto);
    }
}
