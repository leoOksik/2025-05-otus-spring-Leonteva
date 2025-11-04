package ru.otus.hw.controllers;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.ValidationId;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/books")
public class BookController {
    private final BookService bookService;

    @PostMapping
    public Mono<ResponseEntity<BookResponseDto>> createBook(@Validated({Default.class, ValidationId.OnCreate.class})
                                                            @RequestBody BookRequestDto bookRequestDto) {
        return bookService.insert(Mono.just(bookRequestDto))
            .map(savedBook -> ResponseEntity.status(HttpStatus.CREATED).body(savedBook));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<BookResponseDto>>> getBooks() {
        return Mono.just(ResponseEntity.ok().body(bookService.findAll()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<BookResponseDto>> getBook(@PathVariable("id") Long id) {
        return bookService.findById(id).map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<BookResponseDto>> editBook(@PathVariable("id") Long id,
                                                          @Validated({Default.class, ValidationId.OnUpdate.class})
                                                          @RequestBody BookRequestDto bookRequestDto) {
        return bookService.update(id, Mono.just(bookRequestDto)).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBook(@PathVariable("id") Long id) {
        return bookService.deleteById(id).then(Mono.just(ResponseEntity.noContent().build()));
    }
}
