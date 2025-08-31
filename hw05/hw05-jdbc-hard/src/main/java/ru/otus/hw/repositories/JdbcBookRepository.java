package ru.otus.hw.repositories;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

import java.util.stream.Collectors;

@SuppressWarnings("ClassCanBeRecord")
@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public Optional<Book> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Book id must not be null");
        }
        Optional<Book> book = jdbcOperations.query("""
            SELECT b.id, b.title, a.id as author_id, a.full_name as author_name
            FROM books b
            LEFT JOIN authors a ON b.author_id = a.id
            WHERE b.id=:id""", Map.of("id", id), new BookResultSetExtractor());

        if (book != null && book.isPresent()) {

            List<BookGenreRelation> bookGenreRelations = jdbcOperations.query("""
                SELECT book_id, genre_id FROM books_genres
                WHERE book_id=:id""", Map.of("id", id), new BookGenreRelationMapper());

            if (!bookGenreRelations.isEmpty()) {
                book.get().setGenres(
                    genreRepository.findAllByIds(bookGenreRelations.stream()
                        .map(BookGenreRelation::genreId).collect(Collectors.toSet())));
            }
        }
        return book;
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        int deletedRow = jdbcOperations.update("""
                DELETE
                FROM books
                WHERE id=:id
                """,
            Map.of("id", id));

        if (deletedRow == 0) {
            throw new EntityNotFoundException("Entity not found");
        }
    }

    private List<Book> getAllBooksWithoutGenres() {
        return jdbcOperations.query("""
                SELECT b.id,
                       b.title,
                       a.id as author_id,
                       a.full_name as author_name
                FROM books b
                LEFT JOIN authors a ON b.author_id = a.id
                """,
            new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbcOperations.query("""
                SELECT book_id,
                       genre_id
                FROM books_genres
                """,
            new BookGenreRelationMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {

        Map<Long, Genre> genreMap = genres.stream().collect(Collectors.toMap(Genre::getId, genre -> genre));

        for (Book book : booksWithoutGenres) {
            List<Genre> genresList = new ArrayList<>();
            for (BookGenreRelation relation : relations) {
                if (Objects.equals(relation.bookId, book.getId())) {
                    Optional.ofNullable(genreMap.get(relation.genreId))
                        .ifPresent(genresList::add);
                }
            }
            book.setGenres(genresList);
        }
    }

    private Book insert(@Valid @NotNull Book book) {
        if (CollectionUtils.isEmpty(book.getGenres())) {
            throw new IllegalArgumentException("Genres must not be empty");
        }
        var keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("title", book.getTitle())
            .addValue("author_id", book.getAuthor().getId());

        jdbcOperations.update("""
            INSERT INTO books (title,author_id)
            VALUES(:title, :author_id)""", params, keyHolder, new String[]{"id"});

        //noinspection DataFlowIssue
        Long id = keyHolder.getKeyAs(Long.class);
        if (id == null) {
            throw new IllegalStateException("Failed return id from db");
        }
        book.setId(id);
        batchInsertGenresRelationsFor(book.getId(),
            book.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));

        return book;
    }

    private Book update(@Valid @NotNull Book book) {
        if (CollectionUtils.isEmpty(book.getGenres())) {
            throw new IllegalArgumentException("Genres must not be empty");
        }
        Map<String, Object> params = Map.of(
            "id", book.getId(), "title", book.getTitle(), "author_id", book.getAuthor().getId());

        int rowUpdate = jdbcOperations.update("""
                UPDATE books SET title = :title, author_id = :author_id
                WHERE id=:id""", params);

        if (rowUpdate == 0) {
            throw new EntityNotFoundException("Entity not found. Row wasn't updated");
        }

        Map<String, Set<Long>> mapGenres = findChangesBookGenreRelations(book);
        if (mapGenres.containsKey("add")) {
            batchInsertGenresRelationsFor(book.getId(), mapGenres.get("add"));
        }
        if (mapGenres.containsKey("remove")) {
            removeGenresRelationsFor(book.getId(), mapGenres.get("remove"));
        }
        return book;
    }

    private Map<String, Set<Long>> findChangesBookGenreRelations(Book book) {
        Map<String, Object> params = Map.of("id", book.getId());

        Set<Long> oldGenres = new HashSet<>(jdbcOperations.query("""
            SELECT genre_id from books_genres
            WHERE book_id=:id
            """, params, ((rs, rowNum) -> rs.getLong("genre_id"))));

        Set<Long> newGenres = book.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());

        return Map.of("add", newGenres.stream().filter(id -> !oldGenres.contains(id)).collect(Collectors.toSet()),
            "remove", oldGenres.stream().filter(id -> !newGenres.contains(id)).collect(Collectors.toSet()));
    }

    private void batchInsertGenresRelationsFor(Long bookId, Set<Long> genresIds) {

        MapSqlParameterSource[] params = genresIds.stream()
            .map(genreId -> new MapSqlParameterSource()
                .addValue("book_id", bookId)
                .addValue("genre_id", genreId))
            .toArray(MapSqlParameterSource[]::new);

        jdbcOperations.batchUpdate("""
                INSERT
                INTO books_genres (book_id, genre_id)
                VALUES (:book_id, :genre_id)
                """,
            params);
    }

    private void removeGenresRelationsFor(Long bookId, Set<Long> genresIds) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("book_id", bookId)
            .addValue("genre_ids", genresIds);

        jdbcOperations.update("""
            DELETE FROM books_genres bg
            WHERE bg.book_id = :book_id AND genre_id IN (:genre_ids)
            """, params);
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Book(rs.getLong("id"), rs.getString("title"),
                new Author(rs.getLong("author_id"), rs.getString("author_name")),
                Collections.emptyList());
        }
    }

    private static class BookGenreRelationMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id"));
        }
    }

    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Optional<Book>> {

        @Override
        public Optional<Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(new Book(rs.getLong("id"), rs.getString("title"),
                new Author(rs.getLong("author_id"), rs.getString("author_name")),
                Collections.emptyList()));
        }
    }

    private record BookGenreRelation(Long bookId, Long genreId) {
    }
}
