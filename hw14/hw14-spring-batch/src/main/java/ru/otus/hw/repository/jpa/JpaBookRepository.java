package ru.otus.hw.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.hw.model.jpa.Book;

import java.util.List;

public interface JpaBookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.author JOIN FETCH b.genres")
    @Override
    List<Book> findAll();
}
