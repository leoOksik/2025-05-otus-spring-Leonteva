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
    public Mono<ResponseEntity<AuthorDto>> addAuthor(@Validated({Default.class, ValidationId.OnCreate.class})
                                                   @RequestBody AuthorDto authorDto) {
        return authorService.insert(Mono.just(authorDto))
            .map(savedAuthor -> ResponseEntity.status(HttpStatus.CREATED).body(savedAuthor));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<AuthorDto>>> getAuthors() {
        return Mono.just(ResponseEntity.ok().body(authorService.findAll()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AuthorDto>> getAuthor(@PathVariable ("id") Long id) {
        return authorService.findById(id).map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<AuthorDto>> editAuthor(@PathVariable ("id") Long id,
                                                @Validated({Default.class, ValidationId.OnUpdate.class})
                                                @RequestBody AuthorDto authorDto) {
        return authorService.update(id, Mono.just(authorDto)).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAuthor(@PathVariable ("id") Long id) {
        return authorService.deleteById(id).then(Mono.just(ResponseEntity.noContent().build()));
    }
}
