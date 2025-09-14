package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaBookRepository implements BookRepository {

    public static final String ENTITY_GRAPH = "author-entity-graph";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Book> findById(Long id) {
        EntityGraph<?> entityGraph = entityManager.getEntityGraph(ENTITY_GRAPH);
        return entityManager.createQuery("""
                SELECT b FROM Book b
                WHERE b.id = :id
                """, Book.class)
            .setParameter("id", id)
            .setHint("jakarta.persistence.fetchgraph", entityGraph)
            .getResultStream()
            .findFirst();
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> entityGraph = entityManager.getEntityGraph(ENTITY_GRAPH);
        return entityManager.createQuery("""
                SELECT b FROM Book b
                """, Book.class)
            .setHint("jakarta.persistence.fetchgraph", entityGraph)
            .getResultList();
    }

    @Override
    public void deleteById(Long id) {
        var book = entityManager.find(Book.class, id);
        if (book != null) {
            entityManager.remove(book);
        }
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            entityManager.persist(book);
            return book;
        } else {
            return entityManager.merge(book);
        }
    }
}
