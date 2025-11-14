package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public  Flux<GenreDto> getGenres() {
        return genreService.findAll();
    }
}
