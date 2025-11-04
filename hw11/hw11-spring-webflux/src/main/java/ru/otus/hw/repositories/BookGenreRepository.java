package ru.otus.hw.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.BookGenre;

public interface BookGenreRepository extends ReactiveCrudRepository<BookGenre, Long> {
    Flux<Object> deleteByBookId(Long id);
}
