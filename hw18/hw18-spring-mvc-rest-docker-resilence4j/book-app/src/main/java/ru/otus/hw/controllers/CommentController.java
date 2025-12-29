package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.service.CommentServiceRestClientImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/comments")
public class CommentController {

    private final CommentServiceRestClientImpl commentServiceRestClient;

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<CommentDto>> getCommentsByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(commentServiceRestClient.findByBookId(bookId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long id) {
        commentServiceRestClient.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
