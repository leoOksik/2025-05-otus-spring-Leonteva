package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mappers.GenreMapper;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream().map(genreMapper::toDto).toList();
    }

    @Override
    public List<GenreDto> findAllByIdIn(Set<Long> ids) {
        Objects.requireNonNull(ids, "Ids must not be null");

        if (ids.contains(null)) {
            throw  new IllegalArgumentException("Ids set contains null");
        }
        return genreRepository.findAllByIdIn(ids).stream().map(genreMapper::toDto).toList();
    }
}
