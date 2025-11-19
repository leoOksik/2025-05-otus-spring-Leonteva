package ru.otus.hw.services.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CommentServiceSecurityTest {

    @Autowired
    private CommentService commentService;

    @DisplayName("должен разрешить добавление комментария для ролей ADMIN и USER")
    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void shouldAllowInsertionCommentForAllRole() {
        assertDoesNotThrow(() ->
            commentService.insert(new CommentDto(null, "New_comment", 2L)));
    }

    @DisplayName("должен разрешить поиск комментария по id для ролей ADMIN и USER")
    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void shouldAllowFindCommentByIdForRoleAdmin() {
        assertDoesNotThrow(() -> commentService.findById(1L));
    }

    @DisplayName("должен разрешить поиск комментариев по id книги для ролей ADMIN и USER")
    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void shouldAllowFindAllCommentsByBookIdForRoleUser() {
        assertDoesNotThrow(() -> commentService.findByBookId(2L));
    }

    @DisplayName("должен запретить обновления комментария для роли USER")
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyUpdateCommentForRoleUser() {
        assertThrows(AccessDeniedException.class, ()
            -> commentService.update(1L, new CommentDto(1L, "Test_update_text", 2L)));
    }

    @DisplayName("должен разрешить обновления комментария для роли ADMIN")
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowUpdateCommentForRoleAdmin() {
        assertDoesNotThrow(() -> commentService.update(1L,
            new CommentDto(1L, "Test_update_text", 2L)));
    }

    @DisplayName("должен запретить удаление комментария для роли USER")
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyDeleteCommentForRoleUser() {
        assertThrows(AccessDeniedException.class, () -> commentService.deleteById(3L));
    }

    @DisplayName("должен разрешить удаление комментария для роли ADMIN")
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowDeleteCommentForRoleAdmin() {
        assertDoesNotThrow(() -> commentService.deleteById(3L));
    }
}
