package ru.otus.hw.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.CommentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/book")
    public String commentList(Model model, @RequestParam("id") Long id) {
        List<CommentDto> comments = commentService.findByBookId(id);
        model.addAttribute("comments", comments);
        return "comment-list";
    }

    @PostMapping("/delete")
    public String deleteComment(@RequestParam("id") Long id, Model model) {
        Long bookId = commentService.findById(id).orElseThrow(NotFoundException::new).getBook().getId();
        commentService.deleteById(id);
        return "redirect:/comments/book?id=" + bookId;
    }
}
