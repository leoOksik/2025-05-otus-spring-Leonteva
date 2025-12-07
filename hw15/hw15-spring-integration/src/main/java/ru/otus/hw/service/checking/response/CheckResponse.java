package ru.otus.hw.service.checking.response;

public record CheckResponse(Boolean verified, Long applicationId, String comment)  {}
