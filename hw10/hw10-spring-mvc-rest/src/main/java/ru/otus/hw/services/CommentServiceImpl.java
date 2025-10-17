package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.CommentMapper;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Override
    public Optional<CommentDto> findById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return commentRepository.findById(id).map(commentMapper::toDto);
    }

    @Override
    public List<CommentDto> findByBookId(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return commentRepository.findByBookId(id).stream().map(commentMapper::toDto).toList();
    }

    @Transactional
    @Override
    public CommentDto insert(CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    @Transactional
    @Override
    public CommentDto update(CommentDto commentDto) {
        if (!commentRepository.existsById(commentDto.getId())) {
            throw new NotFoundException("Comment not found");
        }
        Comment comment = commentMapper.toEntity(commentDto);
        commentRepository.save(comment);
        return commentMapper.toDto(comment);

    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        if (!commentRepository.existsById(id)) {
            throw new NotFoundException("Comment not found");
        }
        commentRepository.deleteById(id);
    }
}
