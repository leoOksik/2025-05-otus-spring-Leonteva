package ru.otus.hw.controllers;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.ValidationId;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/authors")
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthorDto> addAuthor(@Validated({Default.class, ValidationId.OnCreate.class})
                                                   @RequestBody AuthorDto authorDto) {
        return authorService.insert(Mono.just(authorDto));
    }

    @GetMapping
    public Flux<AuthorDto> getAuthors() {
        return authorService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<AuthorDto> getAuthor(@PathVariable ("id") Long id) {
        return authorService.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<AuthorDto> editAuthor(@PathVariable ("id") Long id,
                                                @Validated({Default.class, ValidationId.OnUpdate.class})
                                                @RequestBody AuthorDto authorDto) {
        return authorService.update(id, Mono.just(authorDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAuthor(@PathVariable ("id") Long id) {
        return authorService.deleteById(id);
    }
}
