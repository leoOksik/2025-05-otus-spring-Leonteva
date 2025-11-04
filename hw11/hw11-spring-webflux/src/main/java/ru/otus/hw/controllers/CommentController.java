package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/book/{bookId}")
    public Mono<ResponseEntity<Flux<CommentDto>>> getCommentsByBookId(@PathVariable ("bookId") Long bookId) {
        return Mono.just(ResponseEntity.ok().body(commentService.findByBookId(bookId)));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteComment(@PathVariable ("id") Long id) {
        return commentService.deleteById(id).then(Mono.just(ResponseEntity.noContent().build()));
    }
}
