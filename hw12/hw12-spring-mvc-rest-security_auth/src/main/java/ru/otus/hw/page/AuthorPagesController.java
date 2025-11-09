package ru.otus.hw.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authors")
public class AuthorPagesController {

    @GetMapping
    public String listAuthorsPage() {
        return "author-list";
    }

    @GetMapping("/create")
    public String addAuthorPage() {
        return "create-author";
    }

    @GetMapping("/edit")
    public String editAuthorPage() {
        return "edit-author";
    }

}
