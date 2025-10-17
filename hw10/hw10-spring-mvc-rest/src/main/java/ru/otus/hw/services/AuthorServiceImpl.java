package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream().map(authorMapper::toDto).toList();
    }

    @Override
    public Optional<AuthorDto> findById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return authorRepository.findById(id).map(authorMapper::toDto);
    }

    @Transactional
    @Override
    public AuthorDto insert(AuthorDto authorDto) {
        Author author = authorMapper.toEntity(authorDto);
        authorRepository.save(author);
        return authorMapper.toDto(author);
    }

    @Transactional
    @Override
    public AuthorDto update(AuthorDto authorDto) {
        if (!authorRepository.existsById(authorDto.getId())) {
            throw new NotFoundException("Author not found");
        }
        Author author = authorMapper.toEntity(authorDto);
        authorRepository.save(author);
        return authorMapper.toDto(author);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        if (!authorRepository.existsById(id)) {
            throw new NotFoundException("Author not found");
        }
        authorRepository.deleteById(id);
    }
}
