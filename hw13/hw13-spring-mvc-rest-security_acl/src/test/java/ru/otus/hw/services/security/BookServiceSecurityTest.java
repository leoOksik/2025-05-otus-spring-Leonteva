package ru.otus.hw.services.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.services.BookService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BookServiceSecurityTest {

    @Autowired
    private BookService bookService;

    @DisplayName("должен запретить добавление книги для роли USER")
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyInsertionBookForRoleUser() {
        assertThrows(AccessDeniedException.class,
            () -> bookService.insert(
                new BookRequestDto(null, "Title_new", 2L, Set.of())));
    }

    @DisplayName("должен разрешить добавление книги для роли ADMIN")
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowInsertionBookForRoleAdmin() {
        assertDoesNotThrow(
            () -> bookService.insert(
                new BookRequestDto(null, "Title_new", 2L, Set.of())));
    }

    @Transactional(readOnly = true)
    @DisplayName("должен разрешить поиск книги по id для ролей ADMIN и USER")
    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void shouldAllowFindBookByIdForAllRoles() {
        assertDoesNotThrow(() -> bookService.findById(1L));
    }

    @Transactional(readOnly = true)
    @DisplayName("должен разрешить получение книг для ролей ADMIN и USER")
    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void shouldAllowFindAllBooksForAllRoles() {
        assertDoesNotThrow(() -> bookService.findAll());
    }

    @DisplayName("должен запретить обновления книги для роли USER")
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyUpdateBookForRoleUser() {
        assertThrows(AccessDeniedException.class, ()
            -> bookService.update(2L,
            new BookRequestDto(2L, "Title_update", 2L, Set.of())));
    }

    @DisplayName("должен разрешить обновления книги для роли ADMIN")
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowUpdateBookForRoleAdmin() {
        assertDoesNotThrow(() -> bookService.update(2L,
            new BookRequestDto(2L, "Title_update", 2L, Set.of())));
    }

    @DisplayName("должен запретить удаление книги для роли USER")
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyDeleteBookForRoleUser() {
        assertThrows(AccessDeniedException.class, () -> bookService.deleteById(3L));
    }

    @DisplayName("должен разрешить удаление книги для роли ADMIN")
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowDeleteBookForRoleAdmin() {
        assertDoesNotThrow(() -> bookService.deleteById(3L));
    }
}
