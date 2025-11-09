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
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.UserDetailsServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({BookController.class, BookMapper.class})
@Import(SecurityConfig.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    private List<BookResponseDto> books;

    @BeforeEach
    void setUp() {
        books = getBooks();
    }


    @DisplayName("должен перенаправлять на страницу аутентификации при запросе получения всех книг")
    @Test
    void shouldRedirectToLoginPageFromGetBooks() throws Exception {
        mvc.perform(get("/api/v1/books"))
            .andExpect(status().is3xxRedirection());
    }

    @DisplayName("должен возвращать книги")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldReturnCorrectlyBooks() throws Exception {
        given(bookService.findAll()).willReturn(books);
        List<BookResponseDto> expectedBooks = books;

        mvc.perform(get("/api/v1/books"))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(expectedBooks)));
    }

    @DisplayName("должен перенаправлять на страницу аутентификации при запросе получения книги по id")
    @Test
    void shouldRedirectToLoginPageFromGetBookById() throws Exception {
        mvc.perform(get("/api/v1/books/{id}", 1L))
            .andExpect(status().is3xxRedirection());
    }

    @DisplayName("должен возвращать книгу по id")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldReturnCorrectlyBookById() throws Exception {
        BookResponseDto book = new BookResponseDto(1L, "Title_test_1", new AuthorDto(), new ArrayList<>());
        given(bookService.findById(1L)).willReturn(book);

        mvc.perform(get("/api/v1/books/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(book)));
    }

    @DisplayName("должен перенаправлять на страницу аутентификации при запросе сохранения новой книги")
    @Test
    void shouldRedirectToLoginPageFromPostBook() throws Exception {
        mvc.perform(post("/api/v1/books"))
            .andExpect(status().is3xxRedirection());
    }

    @DisplayName("должен сохранять новую книгу")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldCorrectlySaveNewBook() throws Exception {
        AuthorDto author = new AuthorDto(1L, "Author_test_1");

        BookRequestDto requestDto = new BookRequestDto(null, "Title_test_1", 1L, Set.of());
        BookResponseDto responseDto = new BookResponseDto(1L, "Title_test_1", author, new ArrayList<>());

        given(bookService.insert(any(BookRequestDto.class))).willReturn(responseDto);

        String expectedRequest = mapper.writeValueAsString(requestDto);
        String expectedResponse = mapper.writeValueAsString(responseDto);

        mvc.perform(post("/api/v1/books")
                .contentType(APPLICATION_JSON)
                .content(expectedRequest))
            .andExpect(status().isCreated())
            .andExpect(content().json(expectedResponse));
    }

    @DisplayName("должен перенаправлять на страницу аутентификации при запросе редактирования книги")
    @Test
    void shouldRedirectToLoginPageFromPutBook() throws Exception {
        mvc.perform(put("/api/v1/books/{id}", 1L))
            .andExpect(status().is3xxRedirection());
    }

    @DisplayName("должен обновлять существующую книгу")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldCorrectlyEditBook() throws Exception {
        AuthorDto author = new AuthorDto(1L, "Author_test_1");

        BookRequestDto requestDto = new BookRequestDto(1L, "Title_test_1", 1L, Set.of());
        BookResponseDto responseDto = new BookResponseDto(1L, "Title_test_1", author, new ArrayList<>());

        given(bookService.update(eq(1L), any(BookRequestDto.class))).willReturn(responseDto);

        String expectedRequest = mapper.writeValueAsString(requestDto);
        String expectedResponse = mapper.writeValueAsString(responseDto);

        mvc.perform(put("/api/v1/books/{id}", 1L)
                .contentType(APPLICATION_JSON)
                .content(expectedRequest))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse));
    }

    @DisplayName("должен перенаправлять на страницу аутентификации при запросе удаления книги")
    @Test
    void shouldRedirectToLoginPageFromDeleteBook() throws Exception {
        mvc.perform(delete("/api/v1/books/{id}", 2L))
            .andExpect(status().is3xxRedirection());
    }

    @DisplayName("должен удалять существующую книгу")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldCorrectlyDeleteBook() throws Exception {
        doNothing().when(bookService).deleteById(2L);
        mvc.perform(delete("/api/v1/books/{id}", 2L))
            .andExpect(status().isNoContent());
    }

    @DisplayName("должен возвращать статуc 404 и сообщение \"Book not found\", " +
        "если книга не найдена по переданному id при обновлении")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldReturnExpectedErrorWhenBooksNotFoundForUpdate() throws Exception {
        given(bookService.update(eq(5L), any(BookRequestDto.class)))
            .willThrow(new NotFoundException("Book not found"));

        mvc.perform(put("/api/v1/books/{id}", 5L)
                .contentType(APPLICATION_JSON)
                .content("{\"id\": 5, \"title\": \"Test Title\", \"authorId\": 1, \"genreIds\": []}"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Book not found"));
    }

    @DisplayName("должен возвращать статуc 404 и сообщение \"Book not found\", " +
        "если книга не найдена по переданному id при удалении")
    @WithMockUser(username = "user_login_1")
    @Test
    void shouldReturnExpectedErrorWhenBooksNotFoundForDelete() throws Exception {
        willThrow(new NotFoundException("Book not found")).given(bookService).deleteById(5L);

        mvc.perform(delete("/api/v1/books/{id}", 5L))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Book not found"));
    }

    private List<BookResponseDto> getBooks() {
        AuthorDto author = new AuthorDto(1L, "Author_test");
        List<GenreDto> genres = new ArrayList<>();

        return List.of(
            new BookResponseDto(1L, "Title_test_1", author, genres),
            new BookResponseDto(2L, "Title_test_2", author, genres));
    }
}
