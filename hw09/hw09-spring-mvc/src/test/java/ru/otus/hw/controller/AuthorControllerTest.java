package ru.otus.hw.controller;

import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest({AuthorController.class, AuthorMapper.class})
public class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthorService authorService;

    private List<AuthorDto> authors;

    @BeforeEach
    void setUp() {
       authors = List.of(new AuthorDto(1L, "Author_1"),
           new AuthorDto(2L, "Author_2"));
    }

    @Test
    void shouldRenderListPageWithCorrectViewAndModelAttributes() throws Exception {
        when(authorService.findAll()).thenReturn(authors);
        List<AuthorDto> expectedAuthors = authors;

        mvc.perform(get("/authors"))
            .andExpect(view().name("author-list"))
            .andExpect(model().attribute("authors", expectedAuthors));
    }


    @Test
    void shouldRenderEditPageWithCorrectViewAndModelAttributes() throws Exception {
        when(authorService.findById(1L)).thenReturn(Optional.of(authors.get(0)));
        AuthorDto expectedAuthor  = authors.get(0);

        mvc.perform(get("/authors/edit").param("id", "1"))
            .andExpect(view().name("edit-author"))
            .andExpect(model().attribute("author", expectedAuthor));
    }

    @Test
    void shouldRenderErrorPageWhenAuthorNotFound() throws Exception {
        when(authorService.findById(1L)).thenThrow(new NotFoundException());

        mvc.perform(get("/authors/edit").param("id", "1"))
            .andExpect(view().name("custom-error"));

        mvc.perform(post("/authors/delete").param("id", "1"))
            .andExpect(view().name("custom-error"));
    }

    @Test
    void shouldSaveAuthorAndRedirectToContextPath() throws Exception {
        when(authorService.findById(1L)).thenReturn(Optional.of(authors.get(0)));

        mvc.perform(post("/authors/edit")
                .param("id", "1").param("fullName", "Author_edit"))
            .andExpect(view().name("redirect:/authors"));

        verify(authorService, times(1)).update(any(AuthorDto.class));
    }

    @Test
    void shouldRenderCreatePageWithCorrectViewAndModelAttributes() throws Exception {
        mvc.perform(get("/authors/create").param("id", "4"))
            .andExpect(view().name("create-author"))
            .andExpect(model().attribute("author",  new AuthorDto()));
    }

    @Test
    void shouldCreateAuthorAndRedirectToContextPath() throws Exception {
       mvc.perform(post("/authors/create").param("id", "4")
           .param("fullName", "Author_4"))
           .andExpect(view().name("redirect:/authors"));

        verify(authorService, times(1)).insert(any(AuthorDto.class));
    }

    @Test
    void shouldDeleteAuthorAndRedirectToContextPath() throws Exception {
        when(authorService.findById(2L)).thenReturn(Optional.of(authors.get(1)));

        mvc.perform(post("/authors/delete")
                .param("id", "2"))
            .andExpect(view().name("redirect:/authors"));

        verify(authorService, times(1)).deleteById(2L);
    }
}
