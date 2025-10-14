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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("author", new AuthorDto());
        return "create-author";
    }

    @PostMapping("/create")
    public String createAuthor(@Valid @ModelAttribute("author") AuthorDto authorDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "create-author";
        }
        authorService.insert(authorDto);
        return "redirect:/authors";
    }

    @GetMapping
    public String authorList(Model model) {
        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "author-list";
    }

    @GetMapping("/edit")
    public String editAuthor(@RequestParam("id") Long id, Model model) {
        AuthorDto authorDto = authorService.findById(id).orElseThrow(NotFoundException::new);
        model.addAttribute("author", authorDto);
        return "edit-author";
    }

    @PostMapping("/edit")
    public String saveEditAuthor(@Valid @ModelAttribute("author") AuthorDto authorDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit-author";
        }
        authorService.update(authorDto);
        return "redirect:/authors";
    }

    @PostMapping("/delete")
    public String deleteAuthor(@RequestParam("id") Long id, Model model) {
        AuthorDto authorDto = authorService.findById(id).orElseThrow(NotFoundException::new);
        authorService.deleteById(authorDto.getId());
        model.addAttribute("author", authorDto);
        return "redirect:/authors";
    }
}
