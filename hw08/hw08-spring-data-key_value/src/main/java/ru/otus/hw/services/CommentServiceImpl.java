package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    public Optional<Comment> findById(String id) {
        Objects.requireNonNull(id, "Id must not be null");
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> findByBookId(String id) {
        Objects.requireNonNull(id, "Id must not be null");
        return commentRepository.findByBookId(id);
    }

    @Override
    public Comment insert(String text, String bookId) {
        return save(null, text, bookId);
    }

    @Override
    public Comment update(String id, String text, String bookId) {
        return save(id, text, bookId);
    }

    private Comment save(String id, String text, String bookId) {
        Objects.requireNonNull(bookId, "Book id must not be null");
        Objects.requireNonNull(text, "Comment text must not be null");

        var book = bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));

        var comment = new Comment(id, text, book);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteById(String id) {
        Objects.requireNonNull(id, "Id must not be null");
        commentRepository.deleteById(id);
    }
}
