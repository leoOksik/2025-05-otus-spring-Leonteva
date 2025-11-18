package ru.otus.hw.controllers;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.ValidationId;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/authors")
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping

    public ResponseEntity<AuthorDto> addAuthor(@Validated({Default.class, ValidationId.OnCreate.class})
                                                   @RequestBody AuthorDto authorDto) {
        final AuthorDto author = authorService.insert(authorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(author);
    }

    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAuthors() {
        final List<AuthorDto> authors = authorService.findAll();
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity <AuthorDto> getAuthor(@PathVariable Long id) {
        final AuthorDto author =  authorService.findById(id);
        return ResponseEntity.ok(author);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> editAuthor(@PathVariable Long id,
                                                @Validated({Default.class, ValidationId.OnUpdate.class})
                                                @RequestBody AuthorDto authorDto) {
        final AuthorDto updatedAuthor = authorService.update(id, authorDto);
        return ResponseEntity.ok(updatedAuthor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAuthor(@PathVariable Long id) {
        authorService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
