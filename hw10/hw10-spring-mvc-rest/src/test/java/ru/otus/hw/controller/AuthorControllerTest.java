package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.AuthorController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest({AuthorController.class, AuthorMapper.class})
public class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthorService authorService;

    @Autowired
    private ObjectMapper mapper;

    private List<AuthorDto> authors;

    @BeforeEach
    void setUp() {
        authors = List.of(new AuthorDto(1L, "Author_1"),
            new AuthorDto(2L, "Author_2"));
    }

    @DisplayName("должен возвращать авторов")
    @Test
    void shouldReturnCorrectlyAuthors() throws Exception {
        given(authorService.findAll()).willReturn(authors);
        List<AuthorDto> expectedAuthors = authors;

        mvc.perform(get("/api/v1/authors"))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(expectedAuthors)));
    }

    @DisplayName("должен возвращать автора по id")
    @Test
    void shouldReturnCorrectlyAuthorById() throws Exception {
        given(authorService.findById(1L)).willReturn(authors.get(0));
        AuthorDto expectedAuthor = authors.get(0);

        mvc.perform(get("/api/v1/authors/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(expectedAuthor)));
    }

    @DisplayName("должен сохранять нового автора")
    @Test
    void shouldCorrectlySaveNewAuthor() throws Exception {
        AuthorDto author = new AuthorDto(null, "Author_3");
        given(authorService.insert(any(AuthorDto.class))).willReturn(author);
        String expectedResult = mapper.writeValueAsString(author);

        mvc.perform(post("/api/v1/authors")
                .contentType(APPLICATION_JSON)
                .content(expectedResult))
            .andExpect(status().isCreated())
            .andExpect(content().json(expectedResult));
    }

    @DisplayName("должен обновлять существующего автора")
    @Test
    void shouldCorrectlyEditAuthor() throws Exception {
        AuthorDto author = new AuthorDto(1L, "Author_1_newName");
        given(authorService.update(eq(1L), any(AuthorDto.class))).willReturn(author);
        String expectedResult = mapper.writeValueAsString(author);

        mvc.perform(put("/api/v1/authors/{id}", 1L)
                .contentType(APPLICATION_JSON)
                .content(expectedResult))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResult));
    }

    @DisplayName("должен удалять существующего автора")
    @Test
    void shouldCorrectlyDeleteAuthor() throws Exception {
        doNothing().when(authorService).deleteById(2L);
        mvc.perform(delete("/api/v1/authors/{id}", 2L))
            .andExpect(status().isNoContent());
    }

    @DisplayName("должен возвращать статуc 404 и сообщение \"Author not found\", " +
        "если автор не найден по переданному id при обновлении")
    @Test
    void shouldReturnExpectedErrorWhenAuthorsNotFoundForUpdate() throws Exception {
        given(authorService.update(eq(5L), any(AuthorDto.class)))
            .willThrow(new NotFoundException("Author not found"));

        mvc.perform(put("/api/v1/authors/{id}", 5L)
                .contentType(APPLICATION_JSON)
                .content("{\"id\": 5, \"fullName\": \"Test Author\"}"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Author not found"));

    }

    @DisplayName("должен возвращать статуc 404 и сообщение \"Author not found\", " +
        "если автор не найден по переданному id при удалении")
    @Test
    void shouldReturnExpectedErrorWhenAuthorsNotFoundForDelete() throws Exception {
        willThrow(new NotFoundException("Author not found"))
            .given(authorService)
            .deleteById(5L);

        mvc.perform(delete("/api/v1/authors/{id}", 5L))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Author not found"));
    }
}
