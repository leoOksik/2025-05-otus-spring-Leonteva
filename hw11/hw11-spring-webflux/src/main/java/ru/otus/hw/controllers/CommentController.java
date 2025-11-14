package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/book/{bookId}")
    public Flux<CommentDto> getCommentsByBookId(@PathVariable ("bookId") Long bookId) {
        return commentService.findByBookId(bookId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteComment(@PathVariable ("id") Long id) {
        return commentService.deleteById(id);
    }
}
