package com.jpa.booktracker.dao;

import com.jpa.booktracker.entity.Borrower;
import com.jpa.booktracker.exception.DuplicateEntryException;
import com.jpa.booktracker.exception.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class BorrowerDao {

    @PersistenceContext
    private EntityManager em;

    public BorrowerDao(EntityManager em) {
        if (em == null) {
            throw new IllegalArgumentException("EntityManager cannot be null");
        }

        this.em = em;
    }

    public void addBorrower(Borrower borrower) {
        Long count = em.createQuery(
                "SELECT COUNT(b) FROM Borrower b WHERE b.username = :username", Long.class)
            .setParameter("username", borrower.getUsername())
            .setFlushMode(FlushModeType.COMMIT)
            .getSingleResult();

        if (count != null && count > 0) {
            throw new DuplicateEntryException("Borrower with username " + borrower.getUsername() + " already exists");
        }

        em.persist(borrower);
    }

    public List<Borrower> getAllBorrowers() {
        List<Borrower> result = em.createQuery("SELECT b FROM Borrower b", Borrower.class).getResultList();

        if (result.isEmpty()) {
            throw new EntityNotFoundException("No borrowers found");
        }

        return result;
    }

    public Borrower getBorrowerById(Long id) {
        Borrower result = em.find(Borrower.class, id);
        if (result == null) {
            throw new EntityNotFoundException("Borrower with id " + id + " not found");
        }

        return result;
    }

    public Borrower getBorrowerByUsername(String username) {
        try {
            return em.createQuery("SELECT b FROM Borrower b WHERE b.username = :username", Borrower.class)
                .setParameter("username", username)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("Borrower with username " + username + " not found");
        }
    }

    public List<Borrower> getBorrowersWhoHaveTakenBook(String isbn) {
        List<Borrower> result = em.createQuery("SELECT borrower FROM Borrower borrower " +
                "JOIN Book book WHERE book.isbn = :isbn", Borrower.class)
            .setParameter("isbn", isbn)
            .getResultList();

        if (result.isEmpty()) {
            throw new EntityNotFoundException("No borrowers found for book with isbn " + isbn);
        }

        return result;
    }

    public void updateBorrower(Borrower borrower) {
        getBorrowerById(borrower.getId());
        em.merge(borrower);
    }

    public void deleteBorrower(Borrower borrower) {
        getBorrowerById(borrower.getId());
        em.remove(borrower);
    }

}
