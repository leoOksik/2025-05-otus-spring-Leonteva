package ru.otus.hw.service;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.otus.hw.dto.GenreDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceRestClientImpl implements GenreServiceRestClient {

    private final RestClient restClient;

    @RateLimiter(name = "serviceRateLimiter")
    @Retry(name = "serviceRetry")
    @Override
    public List<GenreDto> findAll() {
        return restClient.get()
                .uri("/genres")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
