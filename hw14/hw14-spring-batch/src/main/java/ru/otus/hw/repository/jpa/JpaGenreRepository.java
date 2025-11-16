package ru.otus.hw.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.model.jpa.Genre;

import java.util.List;
import java.util.Set;

public interface JpaGenreRepository extends JpaRepository<Genre, Long> {
    List<Genre> findAllByIdIn(Set<Long> ids);
}
