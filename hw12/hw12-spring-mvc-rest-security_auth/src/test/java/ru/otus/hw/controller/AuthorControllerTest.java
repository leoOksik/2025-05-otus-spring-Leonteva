package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.controllers.AuthorController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.UserDetailsServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest({AuthorController.class, AuthorMapper.class})
@Import(SecurityConfig.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    private List<AuthorDto> authors;

    @BeforeEach
    void setUp() {
        authors = List.of(new AuthorDto(1L, "Author_1"),
            new AuthorDto(2L, "Author_2"));
    }

    @DisplayName("должен перенаправлять на страницу аутентификации при запросе получения всех авторов")
    @Test
    void shouldRedirectToLoginPageFromGetAuthors() throws Exception {
        mvc.perform(get("/api/v1/authors"))
            .andExpect(status().is3xxRedirection());
    }

    @DisplayName("должен возвращать авторов")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldReturnCorrectlyAuthors() throws Exception {
        given(authorService.findAll()).willReturn(authors);
        List<AuthorDto> expectedAuthors = authors;

        mvc.perform(get("/api/v1/authors"))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(expectedAuthors)));
    }

    @DisplayName("должен перенаправлять на страницу аутентификации при запросе получения автора по id")
    @Test
    void shouldRedirectToLoginPageFromGetAuthorById() throws Exception {
        mvc.perform(get("/api/v1/authors/{id}", 1L))
            .andExpect(status().is3xxRedirection());
    }

    @DisplayName("должен возвращать автора по id")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldReturnCorrectlyAuthorById() throws Exception {
        given(authorService.findById(1L)).willReturn(authors.get(0));
        AuthorDto expectedAuthor = authors.get(0);

        mvc.perform(get("/api/v1/authors/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(expectedAuthor)));
    }

    @DisplayName("должен перенаправлять на страницу аутентификации при запросе сохранения нового автора")
    @Test
    void shouldRedirectToLoginPageFromPostAuthor() throws Exception {
        mvc.perform(post("/api/v1/authors")
                .with(csrf()).param("user", "user"))
            .andExpect(status().is3xxRedirection());
    }

    @DisplayName("должен сохранять нового автора")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldCorrectlySaveNewAuthor() throws Exception {
        AuthorDto author = new AuthorDto(null, "Author_3");
        given(authorService.insert(any(AuthorDto.class))).willReturn(author);
        String expectedResult = mapper.writeValueAsString(author);

        mvc.perform(post("/api/v1/authors")
                .contentType(APPLICATION_JSON)
                .content(expectedResult)
                .with(csrf()).param("user", "user"))
            .andExpect(status().isCreated())
            .andExpect(content().json(expectedResult));
    }

    @DisplayName("должен перенаправлять на страницу аутентификации при запросе редактирования автора")
    @Test
    void shouldRedirectToLoginPageFromPutAuthor() throws Exception {
        mvc.perform(put("/api/v1/authors/{id}", 1L)
                .with(csrf()).param("user", "user"))
            .andExpect(status().is3xxRedirection());
    }

    @DisplayName("должен обновлять существующего автора")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldCorrectlyEditAuthor() throws Exception {
        AuthorDto author = new AuthorDto(1L, "Author_1_newName");
        given(authorService.update(eq(1L), any(AuthorDto.class))).willReturn(author);
        String expectedResult = mapper.writeValueAsString(author);

        mvc.perform(put("/api/v1/authors/{id}", 1L)
                .contentType(APPLICATION_JSON)
                .content(expectedResult)
                .with(csrf()).param("user", "user"))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResult));
    }

    @DisplayName("должен перенаправлять на страницу аутентификации при запросе удаления автора")
    @Test
    void shouldRedirectToLoginPageFromDeleteAuthor() throws Exception {
        mvc.perform(delete("/api/v1/authors/{id}", 2L)
                .with(csrf()).param("user", "user"))
            .andExpect(status().is3xxRedirection());
    }

    @DisplayName("должен удалять существующего автора")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldCorrectlyDeleteAuthor() throws Exception {
        doNothing().when(authorService).deleteById(2L);
        mvc.perform(delete("/api/v1/authors/{id}", 2L)
                .with(csrf()).param("user", "user"))
            .andExpect(status().isNoContent());
    }

    @DisplayName("должен возвращать статуc 404 и сообщение \"Author not found\", " +
        "если автор не найден по переданному id при обновлении")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldReturnExpectedErrorWhenAuthorsNotFoundForUpdate() throws Exception {
        given(authorService.update(eq(5L), any(AuthorDto.class)))
            .willThrow(new NotFoundException("Author not found"));

        mvc.perform(put("/api/v1/authors/{id}", 5L)
                .contentType(APPLICATION_JSON)
                .content("{\"id\": 5, \"fullName\": \"Test Author\"}")
                .with(csrf()).param("user", "user"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Author not found"));

    }

    @DisplayName("должен возвращать статуc 404 и сообщение \"Author not found\", " +
        "если автор не найден по переданному id при удалении")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldReturnExpectedErrorWhenAuthorsNotFoundForDelete() throws Exception {
        willThrow(new NotFoundException("Author not found"))
            .given(authorService)
            .deleteById(5L);

        mvc.perform(delete("/api/v1/authors/{id}", 5L)
                .with(csrf()).param("user", "user"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Author not found"));
    }
}
