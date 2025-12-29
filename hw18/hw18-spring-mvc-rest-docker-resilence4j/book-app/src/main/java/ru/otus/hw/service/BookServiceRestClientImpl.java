package ru.otus.hw.service;


import lombok.RequiredArgsConstructor;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;

import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class BookServiceRestClientImpl implements BookServiceRestClient {

    private final RestClient restClient;
    private final RateLimiter rateLimiter;
    private final Retry retry;

    @Override
    public BookResponseDto findById(Long id) {
        return executeWithResilience(() ->
                restClient.get()
                        .uri("/books/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(BookResponseDto.class)
        );
    }

    @Override
    public List<BookResponseDto> findAll() {
        return executeWithResilience(() ->
                restClient.get()
                        .uri("/books")
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<BookResponseDto>>() {
                        })
        );
    }

    @Override
    public BookResponseDto insert(BookRequestDto bookRequestDto) {
        return executeWithResilience(() ->
                restClient.post()
                        .uri("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(bookRequestDto)
                        .retrieve()
                        .body(BookResponseDto.class)
        );
    }

    @Override
    public BookResponseDto update(Long id, BookRequestDto bookRequestDto) {
        return executeWithResilience(() ->
                restClient.put()
                        .uri("/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(bookRequestDto)
                        .retrieve()
                        .body(BookResponseDto.class)
        );
    }

    @Override
    public void deleteById(Long id) {
        restClient.delete()
                .uri("/books/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    private <T> T executeWithResilience(Supplier<T> operation) {
        return RateLimiter.decorateSupplier(rateLimiter,
                Retry.decorateSupplier(retry, operation)).get();
    }
}
