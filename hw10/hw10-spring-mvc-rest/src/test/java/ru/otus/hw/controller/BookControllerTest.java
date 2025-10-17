package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookResponseDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({BookController.class, BookMapper.class})
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    private List<BookResponseDto> books;

    @BeforeEach
    void setUp() {
        books = getBooks();
    }

    @DisplayName("должен возвращать книги")
    @Test
    void shouldReturnCorrectlyBooks() throws Exception {
        given(bookService.findAll()).willReturn(books);
        List<BookResponseDto> expectedBooks = books;

        mvc.perform(get("/api/v1/books"))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(expectedBooks)));
    }

    @DisplayName("должен возвращать книгу по id")
    @Test
    void shouldReturnCorrectlyBookById() throws Exception {
        BookResponseDto book =  new BookResponseDto(1L, "Title_test_1", new AuthorDto(), new ArrayList<>());
        given(bookService.findById(1L)).willReturn(Optional.of(book));

        mvc.perform(get("/api/v1/books/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(book)));
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldCorrectlySaveNewBook() throws Exception {
        AuthorDto author = new AuthorDto(1L, "Author_test_1");

        BookRequestDto requestDto = new BookRequestDto(1L, "Title_test_1", 1L, Set.of());
        BookResponseDto responseDto = new BookResponseDto(1L, "Title_test_1", author, new ArrayList<>());

        given(bookService.insert(any())).willReturn(responseDto);

        String expectedRequest = mapper.writeValueAsString(requestDto);
        String expectedResponse = mapper.writeValueAsString(responseDto);

        mvc.perform(post("/api/v1/books")
                .contentType(APPLICATION_JSON)
                .content(expectedRequest))
            .andExpect(status().isCreated())
            .andExpect(content().json(expectedResponse));
    }

    @DisplayName("должен обновлять существующую книгу")
    @Test
    void shouldCorrectlyEditBook() throws Exception {
        AuthorDto author = new AuthorDto(1L, "Author_test_1");

        BookRequestDto requestDto = new BookRequestDto(1L, "Title_test_1", 1L, Set.of());
        BookResponseDto responseDto = new BookResponseDto(1L, "Title_test_1", author, new ArrayList<>());

        given(bookService.findById(1L)).willReturn(Optional.of(responseDto));
        given(bookService.update(any())).willReturn(responseDto);

        String expectedRequest = mapper.writeValueAsString(requestDto);
        String expectedResponse = mapper.writeValueAsString(responseDto);

        mvc.perform(put("/api/v1/books/{id}", 1L)
                .contentType(APPLICATION_JSON)
                .content(expectedRequest))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse));
    }

    @DisplayName("должен удалять существующую книгу")
    @Test
    void shouldCorrectlyDeleteBook() throws Exception {
        given(bookService.findById(2L)).willReturn(Optional.of(books.get(1)));
        doNothing().when(bookService).deleteById(2L);
        mvc.perform(delete("/api/v1/books/2")).andExpect(status().isNoContent());
    }

    @DisplayName("должен возвращать статуc 404 и сообщение \"Book not found\", если книга не найдена")
    @Test
    void shouldReturnExpectedErrorWhenBooksNotFound() throws Exception {
        given(bookService.findById(5L)).willReturn(Optional.empty());

        mvc.perform(put("/api/v1/books/5")
                .contentType(APPLICATION_JSON)
                .content("{\"title\": \"Test Title\", \"authorId\": 1, \"genreIds\": []}"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Book not found"));

        mvc.perform(delete("/api/v1/books/5"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Book not found"));
    }

    private List<BookResponseDto> getBooks() {
        AuthorDto author = new AuthorDto();
        List<GenreDto> genres = new ArrayList<>();

        return List.of(
            new BookResponseDto(1L, "Title_test_1",author, genres),
            new BookResponseDto(2L, "Title_test_2", author, genres));
    }
}
