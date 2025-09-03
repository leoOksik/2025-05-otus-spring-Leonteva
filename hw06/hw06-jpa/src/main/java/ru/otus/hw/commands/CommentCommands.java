package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentConverter commentConverter;

    private final CommentService commentService;

    @Transactional(readOnly = true)
    @ShellMethod(value = "Find comment by id", key = "cbi")
    public String findCommentById(Long id) {
        return commentService.findById(id)
            .map(commentConverter::commentToString)
            .orElse("Comment with id %s not found".formatted(id));
    }

    @Transactional(readOnly = true)
    @ShellMethod(value = "Find comments by book id", key = "cbbi")
    public String findCommentsByBookId(Long id) {
        return commentService.findByBookId(id).stream()
            .map(commentConverter::commentToString)
            .collect(Collectors.joining("," + System.lineSeparator()));
    }

    //
    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertComment(String text, Long bookId) {
        var savedComment = commentService.insert(text, bookId);
        return commentConverter.commentToString(savedComment);
    }

    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(Long id, String text, Long bookId) {
        var savedComment = commentService.update(id, text, bookId);
        return commentConverter.commentToString(savedComment);
    }

    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void deleteCommentById(Long id) {
        commentService.deleteById(id);
    }
}
