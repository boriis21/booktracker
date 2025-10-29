package com.jpa.booktracker.dao;

import com.jpa.booktracker.entity.Book;
import com.jpa.booktracker.exception.DuplicateEntryException;
import com.jpa.booktracker.exception.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class BookDao {

    @PersistenceContext
    private EntityManager em;

    public BookDao(EntityManager em) {
        if (em == null) {
            throw new IllegalArgumentException("EntityManager cannot be null");
        }

        this.em = em;
    }

    public void addBook(Book book) {
        Long count = em.createQuery(
                "SELECT COUNT(b) FROM Book b WHERE b.isbn = :isbn", Long.class)
            .setParameter("isbn", book.getIsbn())
            .setFlushMode(FlushModeType.COMMIT)
            .getSingleResult();

        if (count != null && count > 0) {
            throw new DuplicateEntryException("Book with isbn " + book.getIsbn() + " already exists");
        }

        em.persist(book);
    }

    public List<Book> getAllBooks() {
        List<Book> result = em.createQuery("SELECT b FROM Book b", Book.class).getResultList();

        if (result.isEmpty()) {
            throw new EntityNotFoundException("No books found");
        }

        return result;
    }

    public Book getBookById(Long id) {
        Book result = em.find(Book.class, id);
        if (result == null) {
            throw new EntityNotFoundException("Book with id " + id + " not found");
        }

        return result;
    }

    public Book getBookByIsbn(String isbn) {
        try {
            return em.createQuery("SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class)
                .setParameter("isbn", isbn)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("Book with isbn " + isbn + " not found");
        }
    }

    public List<Book> getAllBooksByAuthor(String authorName) {
        List<Book> result = em.createQuery(
            "SELECT b FROM Book b JOIN b.author a WHERE a.name = :authorName", Book.class)
            .setParameter("authorName", authorName)
            .getResultList();

        if (result.isEmpty()) {
            throw new EntityNotFoundException("No books found for author with name " + authorName);
        }

        return result;
    }

    public void updateBook(Book book) {
        em.merge(book);
    }

    public void deleteBook(Book book) {
        if (book == null) {
            return;
        }

        if (book.getAuthor() != null) {
            book.getAuthor().getBooks().remove(book);
        }

        getBookByIsbn(book.getIsbn());
        em.remove(book);
    }

}
