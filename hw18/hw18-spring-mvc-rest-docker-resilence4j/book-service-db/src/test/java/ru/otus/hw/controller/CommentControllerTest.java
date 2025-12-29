package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.CommentService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper mapper;

    private List<CommentDto> comments;

    @BeforeEach
    void setUp() {
        Book book = new Book(1L, "Title_test", new Author(), new ArrayList<>());
        comments = List.of(new CommentDto(1L, "Comment_1", book.getId()),
            new CommentDto(2L, "Comment_2",book.getId()));
    }

    @DisplayName("должен возвращать комментарии по id книги")
    @Test
    void shouldReturnCorrectlyCommentsByBookId() throws Exception {
        given(commentService.findByBookId(1L)).willReturn(comments);
        List<CommentDto> expectedComments = comments;

        mvc.perform(get("/api/v1/comments/book/1"))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(expectedComments)));
    }

    @DisplayName("должен удалять существующий комментарий")
    @Test
    void shouldCorrectlyDeleteComment() throws Exception {
        given(commentService.findById(2L)).willReturn(comments.get(1));
        doNothing().when(commentService).deleteById(2L);
        mvc.perform(delete("/api/v1/comments/2")).andExpect(status().isNoContent());
    }
}
