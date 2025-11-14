package ru.otus.hw.exceptions;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<String>> handleNotFoundException(NotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("You can't delete. Author has books"));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<String>> handleWebExchangeBindException(WebExchangeBindException ex) {
        String message = ex.getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining("; "));
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message));
    }
}
