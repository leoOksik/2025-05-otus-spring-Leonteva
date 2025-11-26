package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream().map(authorMapper::toDto).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public AuthorDto findById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return authorRepository.findById(id).map(authorMapper::toDto)
            .orElseThrow(() -> new NotFoundException("Author not found"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public AuthorDto insert(AuthorDto authorDto) {
        final Author author = authorMapper.toEntity(authorDto);
        final Author savedAuthor = authorRepository.save(author);
        return authorMapper.toDto(savedAuthor);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public AuthorDto update(Long id, AuthorDto authorDto) {
        checkExistingAuthor(id);
        final Author updatedAuthor = authorMapper.toEntity(authorDto);
        updatedAuthor.setId(id);
        final Author savedAuthor = authorRepository.save(updatedAuthor);
        return authorMapper.toDto(savedAuthor);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public void deleteById(Long id) {
        checkExistingAuthor(id);
        authorRepository.deleteById(id);
    }

    private void checkExistingAuthor(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        if (!authorRepository.existsById(id)) {
            throw new NotFoundException("Author not found");
        }
    }
}
