package ru.otus.hw.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest({BookController.class, BookMapper.class})
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    private List<BookDto> books;

    @BeforeEach
    void setUp() {
        books = getBooks();
    }

    @Test
    void shouldRenderListPageWithCorrectViewAndModelAttributes() throws Exception {
        when(bookService.findAll()).thenReturn(books);
        List<BookDto> expectedBooks = books;

        mvc.perform(get("/books"))
            .andExpect(view().name("book-list"))
            .andExpect(model().attribute("books", expectedBooks));
    }

    @Test
    void shouldRenderEditPageWithCorrectViewAndModelAttributes() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.of(books.get(0)));
        BookDto expectedBook = books.get(0);

        mvc.perform(get("/books/edit").param("id", "1"))
            .andExpect(view().name("edit-book"))
            .andExpect(model().attribute("book", expectedBook));
    }

    @Test
    void shouldRenderErrorPageWhenBookNotFound() throws Exception {
        when(bookService.findById(1L)).thenThrow(new NotFoundException());

        mvc.perform(get("/books/edit").param("id", "1"))
            .andExpect(view().name("custom-error"));

        mvc.perform(post("/books/delete").param("id", "1"))
            .andExpect(view().name("custom-error"));
    }

    @Test
    void shouldSaveBookAndRedirectToContextPath() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.of(books.get(0)));

        mvc.perform(post("/books/edit")
                .param("id", "1")
                .param("title", "Title_edit"))
            .andExpect(view().name("redirect:/books"));

        verify(bookService, times(1)).update(any(BookDto.class));
    }

    @Test
    void shouldRenderCreatePageWithCorrectViewAndModelAttributes() throws Exception {
        BookDto book = new BookDto();
        book.setGenres(new ArrayList<>());
        mvc.perform(get("/books/create"))
            .andExpect(view().name("create-book"))
            .andExpect(model().attribute("book", book));
    }

    @Test
    void shouldCreateBookAndRedirectToContextPath() throws Exception {
        mvc.perform(post("/books/create").param("id", "3")
                .param("title", "Title_create"))
            .andExpect(view().name("redirect:/books"));

        verify(bookService, times(1)).insert(any(BookDto.class));
    }

    @Test
    void shouldDeleteBookAndRedirectToContextPath() throws Exception {
        when(bookService.findById(2L)).thenReturn(Optional.of(books.get(1)));

        mvc.perform(post("/books/delete")
                .param("id", "2"))
            .andExpect(view().name("redirect:/books"));

        verify(bookService, times(1)).deleteById(2L);
    }

    private List<BookDto> getBooks() {
        AuthorDto author = new AuthorDto();
        List<GenreDto> genres = new ArrayList<>();

        return List.of(
            new BookDto(1L, "Title_test_1",author, genres),
            new BookDto(2L, "Title_test_2", author, genres));
    }
}
