package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<Comment> findById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return commentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> findByBookId(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return commentRepository.findByBookId(id);
    }

    @Transactional
    @Override
    public Comment insert(String text, Long bookId) {
        return save(null, text, bookId);
    }

    @Transactional
    @Override
    public Comment update(Long id, String text, Long bookId) {
        return save(id, text, bookId);
    }

    public Comment save(Long id, String text, Long bookId) {
        Objects.requireNonNull(bookId, "Comment obj must not be null");
        Objects.requireNonNull(text, "Comment text must not be null");

        var book = bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));

        var comment = new Comment(id, text, book);
        return commentRepository.save(comment);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        commentRepository.deleteById(id);
    }
}
