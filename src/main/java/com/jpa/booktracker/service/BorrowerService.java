package com.jpa.booktracker.service;

import com.jpa.booktracker.dao.BorrowerDao;
import com.jpa.booktracker.entity.Book;
import com.jpa.booktracker.entity.Borrower;

import java.util.List;

public class BorrowerService {

    private final BorrowerDao borrowerDao;

    public BorrowerService(BorrowerDao borrowerDao) {
        if (borrowerDao == null) {
            throw new IllegalArgumentException("BorrowerDao cannot be null");
        }

        this.borrowerDao = borrowerDao;
    }

    public Borrower addBorrower(String username, String firstName, String lastName, Book... borrowedBooks) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }

        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }

        Borrower borrower = new Borrower();
        borrower.setUsername(username);
        borrower.setFirstName(firstName);
        borrower.setLastName(lastName);
        for (Book book : borrowedBooks) {
            if (book == null) {
                throw new IllegalArgumentException("Book cannot be null");
            }

            borrower.borrowBook(book);
        }

        borrowerDao.addBorrower(borrower);
        return borrower;
    }

    public Borrower getBorrowerById(Long id) {
        return borrowerDao.getBorrowerById(id);
    }

    public Borrower getBorrowerByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }

        return borrowerDao.getBorrowerByUsername(username);
    }

    public List<Borrower> getAllBorrowers() {
        return borrowerDao.getAllBorrowers();
    }

    public List<Borrower> getBorrowersWhoHaveTakenBook(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or blank");
        }

        return borrowerDao.getBorrowersWhoHaveTakenBook(isbn);
    }

    public void updateBorrower(Borrower borrower) {
        if (borrower == null) {
            throw new IllegalArgumentException("Borrower cannot be null");
        }

        borrowerDao.updateBorrower(borrower);
    }

    public void deleteBorrower(Borrower borrower) {
        if (borrower == null) {
            throw new IllegalArgumentException("Borrower cannot be null");
        }

        borrowerDao.deleteBorrower(borrower);
    }

}
