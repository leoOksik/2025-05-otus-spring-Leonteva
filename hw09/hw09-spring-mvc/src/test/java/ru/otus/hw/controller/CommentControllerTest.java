package ru.otus.hw.controller;

import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    private List<CommentDto> comments;

    @BeforeEach
    void setUp() {
        Book book = new Book(1L, "Title_test", new Author(), new ArrayList<>());
        comments = List.of(new CommentDto(1L, "Comment_1", book),
            new CommentDto(2L,  "Comment_2", book));
    }

    @Test
    void shouldRenderListPageWithCorrectViewAndModelAttributes() throws Exception {
        when(commentService.findByBookId(1L)).thenReturn(comments);
        List<CommentDto> expectedComments = comments;

        mvc.perform(get("/comments/book")
            .param("id", "1"))
            .andExpect(view().name("comment-list"))
            .andExpect(model().attribute("comments", expectedComments));
    }

    @Test
    void shouldDeleteCommentAndRedirectToContextPath() throws Exception {
        when(commentService.findById(2L)).thenReturn(Optional.of(comments.get(1)));

        mvc.perform(post("/comments/delete")
                .param("id", "2")).andExpect(view()
                .name("redirect:/comments/book?id=" + comments.get(1).getBook().getId()));

        verify(commentService, times(1)).deleteById(2L);
    }
}
