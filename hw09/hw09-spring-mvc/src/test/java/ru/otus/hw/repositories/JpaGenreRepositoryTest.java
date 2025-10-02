package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с жанрами")
@DataJpaTest
public class JpaGenreRepositoryTest {

    @Autowired
    private GenreRepository jpaGenreRepository;

    @Test
    @DisplayName("должен загружать список всех жанров")
    void shouldReturnCorrectGenreList() {
        var returnedGenres = jpaGenreRepository.findAll();

        assertThat(returnedGenres).isNotNull().hasSize(6)
            .allMatch(g -> g.getName() != null && !g.getName().isBlank());
    }

    @Test
    @DisplayName("должен загружать все жанры по переданному набору id")
    void shouldReturnCorrectGenreListByIds() {

        var returnedGenres = jpaGenreRepository.findAll();
        var genresIds = returnedGenres.stream().map(Genre::getId)
            .filter(id -> id % 2 == 0).collect(Collectors.toSet());

        var returnedGenresByIds = jpaGenreRepository.findAllByIdIn(genresIds);

        assertThat(returnedGenresByIds).isNotNull().hasSize(3)
            .allMatch(g -> g.getName() != null && !g.getName().isBlank())
            .allMatch(g -> List.of("Genre_2", "Genre_4", "Genre_6").contains(g.getName()));
    }
}
