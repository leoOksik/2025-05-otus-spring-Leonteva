package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.Objects;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<Book> findById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return bookRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional
    @Override
    public Book insert(String title, Long authorId, Set<Long> genresIds) {
        return save(null, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public Book update(Long id, String title, Long authorId, Set<Long> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        bookRepository.deleteById(id);
    }

    private Book save(Long id, String title, Long authorId, Set<Long> genresIds) {
        Objects.requireNonNull(title, "Title must not be null");
        Objects.requireNonNull(authorId, "Author_id must not be null");
        Objects.requireNonNull(genresIds, "Ids must not be null");

        if (genresIds.contains(null)) {
            throw new IllegalArgumentException("Ids set contains null");
        }

        var author = authorRepository.findById(authorId)
            .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));

        var genres = genreRepository.findAllByIdIn(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new Book(id, title, author, genres);
        return bookRepository.save(book);
    }
}
