package ru.otus.hw.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.spi.Readable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.GenreDto;

import java.io.IOException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private static final String SQL = """
        SELECT
            b.id AS book_id,
            b.title,
            a.id AS author_id,
            a.full_name,
            (
            SELECT
              JSON_ARRAYAGG (JSON_OBJECT('id' VALUE g.id, 'name' VALUE g.name))
              FROM books_genres bg
              JOIN genres g ON g.id = bg.genre_id
              WHERE bg.book_id = b.id
            ) AS genres_json
        FROM books b
        JOIN authors a ON a.id = b.author_id
        """;

    private final R2dbcEntityTemplate template;

    private final ObjectMapper objectMapper;


    @Override
    public Flux<BookResponseDto> findAll() {
        return template.getDatabaseClient()
            .inConnectionMany(conn ->
                Flux.from(conn.createStatement(SQL).execute())
                    .flatMap(result -> result.map(this::mapper))
            );
    }

    @Override
    public Mono<BookResponseDto> findById(Long id) {
        String sql = SQL + " WHERE b.id = ?";

        return template.getDatabaseClient()
            .inConnectionMany(conn ->
                Flux.from(conn.createStatement(sql).bind(0, id).execute())
                    .flatMap(result -> result.map(this::mapper))).next();
    }

    private BookResponseDto mapper(Readable record) {
        String genresJson = record.get("genres_json", String.class);

        try {
            List<GenreDto> genres = objectMapper.readValue(genresJson, new TypeReference<>() {
            });
            return new BookResponseDto(
                record.get("book_id", Long.class), record.get("title", String.class),
                new AuthorDto(record.get("author_id", Long.class), record.get("full_name", String.class)),
                genres
            );
        } catch (IOException ex) {
            throw new RuntimeException("Parsing JSON error: " + ex.getMessage(), ex);
        }
    }

}
