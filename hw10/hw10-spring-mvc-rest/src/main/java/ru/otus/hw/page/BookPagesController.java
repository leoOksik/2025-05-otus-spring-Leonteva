package ru.otus.hw.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/books")
public class BookPagesController {

    @GetMapping
    public String listBooksPage() {
        return "book-list";
    }

    @GetMapping("/create")
    public String addBookPage() {
        return "create-book";
    }

    @GetMapping("/edit")
    public String editBookPage() {
        return "edit-book";
    }

}
