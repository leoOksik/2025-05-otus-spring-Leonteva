package ru.otus.hw.controllers;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.ValidationId;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/books")
public class BookController {
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookResponseDto> createBook(@Validated({Default.class, ValidationId.OnCreate.class})
                                                            @RequestBody BookRequestDto bookRequestDto) {
        return bookService.insert(Mono.just(bookRequestDto));
    }

    @GetMapping
    public Flux<BookResponseDto> getBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<BookResponseDto> getBook(@PathVariable("id") Long id) {
        return bookService.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<BookResponseDto> editBook(@PathVariable("id") Long id,
                                                          @Validated({Default.class, ValidationId.OnUpdate.class})
                                                          @RequestBody BookRequestDto bookRequestDto) {
        return bookService.update(id, Mono.just(bookRequestDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBook(@PathVariable("id") Long id) {
        return bookService.deleteById(id);
    }
}
