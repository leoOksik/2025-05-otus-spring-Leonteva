package ru.otus.hw.service;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.RequestHeadersSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.service.config.TestResilience4jConfig;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = {
        BookServiceRestClientImpl.class,
        TestResilience4jConfig.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookServiceRestClientImplRetryAndRateLimiterTest {

    @Autowired
    private BookServiceRestClientImpl bookService;

    @MockBean
    private RestClient restClient;

    private ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        responseSpec = mock(ResponseSpec.class);
    }

    @Test
    void shouldTemporarilyBlockRequestAfterExceedingRateLimiter() {
        var uriSpec = mock(RequestHeadersUriSpec.class);
        var headersSpec = mock(RequestHeadersSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), anyLong())).thenReturn(headersSpec);
        when(headersSpec.accept(any(MediaType.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(BookResponseDto.class)).thenReturn(new BookResponseDto());

        bookService.findById(1L);
        bookService.findById(2L);

        assertThatThrownBy(() -> bookService.findById(3L)).isInstanceOf(RequestNotPermitted.class);
        verify(restClient, times(2)).get();
    }

    @Test
    void shouldRetryThreeTimesIfNoAnswerFromService() {
        var uriSpec = mock(RequestHeadersUriSpec.class);
        var headersSpec = mock(RequestHeadersSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), anyLong())).thenReturn(headersSpec);
        when(headersSpec.accept(any(MediaType.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(BookResponseDto.class))
                .thenThrow(new ResourceAccessException("No answer"))
                .thenThrow(new ResourceAccessException("No answer"))
                .thenThrow(new ResourceAccessException("No answer"));

        assertThatThrownBy(() -> bookService.findById(1L)).isInstanceOf(ResourceAccessException.class);
        verify(restClient, times(3)).get();
    }
}
