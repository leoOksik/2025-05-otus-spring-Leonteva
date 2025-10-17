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
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.services.AuthorService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
        given(authorService.findById(1L)).willReturn(Optional.of(authors.get(0)));
        AuthorDto expectedAuthor = authors.get(0);

        mvc.perform(get("/api/v1/authors/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(expectedAuthor)));
    }

    @DisplayName("должен сохранять нового автора")
    @Test
    void shouldCorrectlySaveNewAuthor() throws Exception {
        AuthorDto author = new AuthorDto(3L, "Author_3");
        given(authorService.insert(any())).willReturn(author);
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
        given(authorService.findById(1L)).willReturn(Optional.of(author));
        given(authorService.update(any())).willReturn(author);
        String expectedResult = mapper.writeValueAsString(author);

        mvc.perform(put("/api/v1/authors/1")
                .contentType(APPLICATION_JSON)
                .content(expectedResult))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResult));
    }

    @DisplayName("должен удалять существующего автора")
    @Test
    void shouldCorrectlyDeleteAuthor() throws Exception {
        given(authorService.findById(2L)).willReturn(Optional.of(authors.get(1)));
        doNothing().when(authorService).deleteById(2L);
        mvc.perform(delete("/api/v1/authors/2")).andExpect(status().isNoContent());
    }

    @DisplayName("должен возвращать статуc 404 и сообщение \"Author not found\", если автор не найден")
    @Test
    void shouldReturnExpectedErrorWhenAuthorsNotFound() throws Exception {
        given(authorService.findById(5L)).willReturn(Optional.empty());

        mvc.perform(put("/api/v1/authors/5")
                .contentType(APPLICATION_JSON)
                .content("{\"fullName\": \"Test Author\"}"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Author not found"));

        mvc.perform(delete("/api/v1/authors/5"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Author not found"));
    }
}
