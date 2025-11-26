package ru.otus.hw.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comments")
public class CommentPagesController {

    @GetMapping
    public String listCommentsPage() {
        return "comment-list";
    }
}
