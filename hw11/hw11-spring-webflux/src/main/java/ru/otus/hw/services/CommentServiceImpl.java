package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.CommentMapper;
import ru.otus.hw.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Override
    public Mono<CommentDto> findById(Long id) {
        Objects.requireNonNull(id, "Id must not be null");
        return commentRepository.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("Comment not found")))
            .map(commentMapper::toDto);
    }

    @Override
    public Flux<CommentDto> findByBookId(Long bookId) {
        Objects.requireNonNull(bookId, "BookId must not be null");
        return commentRepository.findByBookId(bookId)
            .map(commentMapper::toDto);
    }

    @Transactional
    @Override
    public  Mono<CommentDto> insert(Mono<CommentDto> commentDto) {
        return commentDto.map(commentMapper::toEntity)
            .flatMap(commentRepository::save).map(commentMapper::toDto);
    }

    @Transactional
    @Override
    public  Mono<CommentDto> update(Long id,  Mono<CommentDto> commentDto) {
        return checkExistingComment(id).then(commentDto)
            .map(commentMapper::toEntity)
            .doOnNext(comment -> comment.setId(id))
            .flatMap(commentRepository::save).map(commentMapper::toDto);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(Long id) {
        return checkExistingComment(id).then(commentRepository.deleteById(id));
    }

    private Mono<Void> checkExistingComment(Long id) {
        return commentRepository.existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new NotFoundException("Comment not found"));
                }
                return Mono.empty();
            });
    }
}
