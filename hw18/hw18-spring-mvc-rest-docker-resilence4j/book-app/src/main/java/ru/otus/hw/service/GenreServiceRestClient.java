package ru.otus.hw.service;

import ru.otus.hw.dto.GenreDto;

import java.util.List;

public interface GenreServiceRestClient {

    List<GenreDto> findAll();
}
