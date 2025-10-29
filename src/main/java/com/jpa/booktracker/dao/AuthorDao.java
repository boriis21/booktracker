package com.jpa.booktracker.dao;

import com.jpa.booktracker.entity.Author;
import com.jpa.booktracker.exception.DuplicateEntryException;
import com.jpa.booktracker.exception.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class AuthorDao {

    @PersistenceContext
    private EntityManager em;

    public AuthorDao(EntityManager em) {
        if (em == null) {
            throw new IllegalArgumentException("EntityManager cannot be null");
        }

        this.em = em;
    }

    public void addAuthor(Author author) {
        Long count = em.createQuery(
                "SELECT COUNT(a) FROM Author a WHERE a.name = :name", Long.class)
            .setParameter("name", author.getName())
            .setFlushMode(FlushModeType.COMMIT)
            .getSingleResult();

        if (count != null && count > 0) {
            throw new DuplicateEntryException("Author with name " + author.getName() + " already exists");
        }

        em.persist(author);
    }

    public List<Author> getAllAuthors() {
        List<Author> result = em.createQuery("SELECT a FROM Author a", Author.class).getResultList();

        if (result.isEmpty()) {
            throw new EntityNotFoundException("No authors found");
        }

        return result;
    }

    public Author getAuthorById(Long id) {
        Author result = em.find(Author.class, id);
        if (result == null) {
            throw new EntityNotFoundException("Author with id " + id + " not found");
        }

        return result;
    }

    public Author getAuthorByName(String name) {
        try {
            return em.createQuery("SELECT a FROM Author a WHERE a.name = :name", Author.class)
                .setParameter("name", name)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("Author with name " + name + " not found");
        }
    }

    public void updateAuthor(Author author) {
        em.merge(author);
    }

    public void deleteAuthor(Author author) {
        getAuthorById(author.getId());
        em.remove(author);
    }

}
