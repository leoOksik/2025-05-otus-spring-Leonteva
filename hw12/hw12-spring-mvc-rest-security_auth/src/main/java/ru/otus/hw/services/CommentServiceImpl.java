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

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Override
    public CommentDto findById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return commentRepository.findById(id).map(commentMapper::toDto)
            .orElseThrow(() -> new NotFoundException("Comment not found"));
    }

    @Override
    public List<CommentDto> findByBookId(Long bookId) {
        Objects.requireNonNull(bookId, "BookId must not be null");
        return commentRepository.findByBookId(bookId).stream().map(commentMapper::toDto).toList();
    }

    @Transactional
    @Override
    public CommentDto insert(CommentDto commentDto) {
        final Comment comment = commentMapper.toEntity(commentDto);
        final Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Transactional
    @Override
    public CommentDto update(Long id, CommentDto commentDto) {
        checkExistingComment(id);
        final Comment updatedComment = commentMapper.toEntity(commentDto);
        updatedComment.setId(id);
        final Comment savedComment = commentRepository.save(updatedComment);
        return commentMapper.toDto(savedComment);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        checkExistingComment(id);
        commentRepository.deleteById(id);
    }

    private void checkExistingComment(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        if (!commentRepository.existsById(id)) {
            throw new NotFoundException("Comment not found");
        }
    }
}
