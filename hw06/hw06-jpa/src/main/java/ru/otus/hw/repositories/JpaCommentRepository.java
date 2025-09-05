package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Comment.class, id));
    }

    @Override
    public List<Comment> findByBookId(Long id) {
        return entityManager.createQuery("""
                SELECT c FROM Comment c
                WHERE c.book.id = :id
                """, Comment.class)
            .setParameter("id", id)
            .getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            entityManager.persist(comment);
            return comment;
        } else {
            return entityManager.merge(comment);
        }
    }

    @Override
    public void deleteById(Long id) {
        Comment comment = entityManager.find(Comment.class, id);
        if (comment != null) {
            entityManager.remove(comment);
        }
    }
}
