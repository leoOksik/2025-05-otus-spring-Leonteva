package ru.otus.hw.service;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.otus.hw.dto.AuthorDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceRestClientImpl implements AuthorServiceRestClient {

    private final RestClient restClient;

    @RateLimiter(name = "serviceRateLimiter")
    @Retry(name = "serviceRetry")
    @Override
    public List<AuthorDto> findAll() {
        return restClient.get()
                .uri("/authors")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @RateLimiter(name = "serviceRateLimiter")
    @Retry(name = "serviceRetry")
    @Override
    public AuthorDto findById(Long id) {
        return restClient.get()
                .uri("/authors/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(AuthorDto.class);
    }

    @RateLimiter(name = "serviceRateLimiter")
    @Retry(name = "serviceRetry")
    @Override
    public AuthorDto insert(AuthorDto authorDto) {
        return restClient.post()
                .uri("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .body(authorDto)
                .retrieve()
                .body(AuthorDto.class);
    }

    @RateLimiter(name = "serviceRateLimiter")
    @Retry(name = "serviceRetry")
    @Override
    public AuthorDto update(Long id, AuthorDto authorDto) {
        return restClient.put()
                .uri("/authors/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(authorDto)
                .retrieve()
                .body(AuthorDto.class);
    }

    @RateLimiter(name = "serviceRateLimiter")
    @Retry(name = "serviceRetry")
    @Override
    public void deleteById(Long id) {
        restClient.delete()
                .uri("/authors/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
