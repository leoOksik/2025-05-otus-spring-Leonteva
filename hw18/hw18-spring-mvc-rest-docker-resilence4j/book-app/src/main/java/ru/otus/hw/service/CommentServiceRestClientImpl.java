package ru.otus.hw.service;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.otus.hw.dto.CommentDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceRestClientImpl implements CommentServiceRestClient {

    private final RestClient restClient;

    @RateLimiter(name = "serviceRateLimiter")
    @Retry(name = "serviceRetry")
    @Override
    public List<CommentDto> findByBookId(Long bookId) {
        return restClient.get()
                .uri("/comments/book/{bookId}", bookId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @RateLimiter(name = "serviceRateLimiter")
    @Retry(name = "serviceRetry")
    @Override
    public void deleteById(Long id) {
        restClient.delete()
                .uri("/comments/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
