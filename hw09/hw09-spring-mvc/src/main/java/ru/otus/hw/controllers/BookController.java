package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        BookDto book = new BookDto();
        book.setGenres(new ArrayList<>());
        model.addAttribute("book", book);
        fillForm(model);
        return "create-book";
    }

    @PostMapping("/create")
    public String createAuthor(@Valid @ModelAttribute("book") BookDto bookDto, BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            fillForm(model);
            return "create-book";
        }
        bookService.insert(bookDto);
        return "redirect:/books";
    }

    @GetMapping
    public String bookList(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "book-list";
    }

    @GetMapping("/edit")
    public String editBook(@RequestParam("id") Long id, Model model) {
        BookDto book = bookService.findById(id).orElseThrow(NotFoundException::new);
        model.addAttribute("book", book);
        fillForm(model);
        return "edit-book";
    }

    @PostMapping("/edit")
    public String saveEditBook(@Valid @ModelAttribute("book") BookDto bookDto, BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            fillForm(model);
            return "edit-book";
        }
        bookService.update(bookDto);
        return "redirect:/books";
    }

    @PostMapping("/delete")
    public String deleteAuthor(@RequestParam("id") Long id, Model model) {
        BookDto bookDto = bookService.findById(id).orElseThrow(NotFoundException::new);
        bookService.deleteById(bookDto.getId());
        model.addAttribute("author", bookDto);
        return "redirect:/books";
    }

    private void fillForm(Model model) {
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres",genreService.findAll());
    }
}
