package ru.otus.hw.services.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class AuthorServiceSecurityTest {

    @Autowired
    private AuthorService authorService;

    @DisplayName("должен запретить добавление автора для роли USER")
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyInsertionAuthorForRoleUser() {
        assertThrows(AccessDeniedException.class,
            () -> authorService.insert(new AuthorDto(null, "New_name")));
    }

    @DisplayName("должен разрешить добавление автора для роли ADMIN")
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowInsertionAuthorForRoleAdmin() {
        assertDoesNotThrow(
            () -> authorService.insert(new AuthorDto(null, "New_name")));
    }

    @DisplayName("должен запретить поиск автора по id для роли USER")
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyFindAuthorByIdForRoleUser() {
        assertThrows(AccessDeniedException.class,
            () -> authorService.findById(1L));
    }

    @DisplayName("должен разрешить поиск автора по id для роли ADMIN")
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowFindAuthorByIdForRoleAdmin() {
        assertDoesNotThrow(() -> authorService.findById(1L));
    }

    @DisplayName("должен запретить получение авторов для роли USER")
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyFindAllAuthorsForRoleUser() {
        assertThrows(AccessDeniedException.class, () -> authorService.findAll());
    }

    @DisplayName("должен разрешить получение авторов для роли ADMIN")
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowFindAllAuthorsForRoleUAdmin() {
        assertDoesNotThrow(() -> authorService.findAll());
    }

    @DisplayName("должен запретить обновления автора для роли USER")
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyUpdateAuthorForRoleUser() {
        assertThrows(AccessDeniedException.class, ()
            -> authorService.update(1L, new AuthorDto(1L, "Update_name")));
    }

    @DisplayName("должен разрешить обновления автора для роли ADMIN")
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowUpdateAuthorForRoleAdmin() {
        assertDoesNotThrow(() -> authorService.update(1L, new AuthorDto(1L, "Update_name")));
    }

    @DisplayName("должен запретить удаление автора для роли USER")
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyDeleteAuthorForRoleUser() {
        assertThrows(AccessDeniedException.class, () -> authorService.deleteById(4L));
    }

    @DisplayName("должен разрешить удаление автора для роли ADMIN")
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowDeleteAuthorForRoleAdmin() {
        assertDoesNotThrow(() -> authorService.deleteById(4L));
    }
}
