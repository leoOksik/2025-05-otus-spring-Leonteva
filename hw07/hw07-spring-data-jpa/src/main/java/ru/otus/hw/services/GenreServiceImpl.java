package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public List<Genre> findAllByIdIn(Set<Long> ids) {
        Objects.requireNonNull(ids, "Ids must not be null");

        if (ids.contains(null)) {
            throw  new IllegalArgumentException("Ids set contains null");
        }
        return genreRepository.findAllByIdIn(ids);
    }
}
