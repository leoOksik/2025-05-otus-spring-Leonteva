package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "genres", target = "genreIds", qualifiedByName = "genresToGenreIds")
    BookRequestDto toDto(Book book);

    @Mapping(source = "authorId", target = "author", qualifiedByName = "authorIdToAuthor")
    @Mapping(source = "genreIds", target = "genres", qualifiedByName = "genreIdsToGenres")
    Book toEntity(BookRequestDto dto);

    BookResponseDto toResponseDto(Book book);

    @Named("genresToGenreIds")
    default Set<Long> genresToGenreIds(List<Genre> genres) {
        if (Objects.isNull(genres)) {
            return null;
        }
        return genres.stream().map(Genre::getId).collect(Collectors.toSet());
    }

    @Named("authorIdToAuthor")
    default Author authorIdToAuthor(Long authorId) {
        if ((Objects.isNull(authorId))) {
            return null;
        }
        Author author = new Author();
        author.setId(authorId);
        return author;
    }

    @Named("genreIdsToGenres")
    default List<Genre> genreIdsToGenres(Set<Long> genreIds) {
        if (Objects.isNull(genreIds)) {
            return null;
        }
        return genreIds.stream().map(id -> {
            Genre genre = new Genre();
            genre.setId(id);
            return genre;
        }).collect(Collectors.toList());
    }
}
