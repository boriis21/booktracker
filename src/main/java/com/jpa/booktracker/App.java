package com.jpa.booktracker;

import com.jpa.booktracker.dao.AuthorDao;
import com.jpa.booktracker.dao.BookDao;
import com.jpa.booktracker.dao.BorrowerDao;
import com.jpa.booktracker.entity.Author;
import com.jpa.booktracker.entity.Book;
import com.jpa.booktracker.entity.Borrower;
import com.jpa.booktracker.entity.Genre;
import com.jpa.booktracker.exception.DuplicateEntryException;
import com.jpa.booktracker.exception.EntityNotFoundException;
import com.jpa.booktracker.service.AuthorService;
import com.jpa.booktracker.service.BookService;
import com.jpa.booktracker.service.BorrowerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class App {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("booktracker");
        EntityManager em = emf.createEntityManager();

        AuthorService authorService = new AuthorService(new AuthorDao(em));
        BookService bookService = new BookService(new BookDao(em));
        BorrowerService borrowerService = new BorrowerService(new BorrowerDao(em));

        preloadData(em, authorService, bookService, borrowerService);
        runShowcase(em, authorService, bookService, borrowerService);

        em.close();
        emf.close();
    }

    private static void preloadData(EntityManager em, AuthorService authorService, BookService bookService,
                                    BorrowerService borrowerService) {

        try {
            em.getTransaction().begin();

            Author tolkien = authorService
                .addAuthor("J.R.R. Tolkien", "British author of LOTR and The Hobbit.");
            Author orwell = authorService
                .addAuthor("George Orwell", "English novelist, author of 1984 and Animal Farm.");
            Author rowling = authorService
                .addAuthor("J.K. Rowling", "British author of the Harry Potter series.");

            bookService.addBook("The Hobbit", "9780547928227", tolkien, Genre.FANTASY);
            bookService.addBook("The Lord of the Rings", "9780618640157", tolkien, Genre.FANTASY);
            bookService.addBook("1984", "9780451524935", orwell, Genre.DYSTOPIA);
            bookService.addBook("Animal Farm", "9780451526342", orwell, Genre.DYSTOPIA);
            bookService.addBook("Harry Potter and the Philosopher's Stone", "9780747532699", rowling, Genre.FANTASY);

            borrowerService.addBorrower("alice", "Alice", "Johnson");
            borrowerService.addBorrower("bob", "Bob", "Smith");

            em.getTransaction().commit();
            System.out.println("Sample data preloaded successfully.");
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Failed to preload data: " + e.getMessage());
        }
    }

    private static void runShowcase(EntityManager em, AuthorService authorService,
                                    BookService bookService, BorrowerService borrowerService) {

        printInitialState(authorService, bookService);
        borrowSomeBooks(em, borrowerService, bookService);
        printBooksWithBorrowers(bookService);
        demonstrateIdempotency(em, borrowerService, bookService);
        printBooksWithBorrowers(bookService);
        removeBorrowerFromBook(em, borrowerService, bookService);
        printBooksWithBorrowers(bookService);
        printFinalStatus(authorService, bookService, borrowerService);
    }

    private static void printInitialState(AuthorService authorService, BookService bookService) {
        try {
            System.out.println("-- Initial authors and their books --");
            authorService.getAllAuthors().forEach(a -> System.out.println(a.toString()));
            System.out.println();

            System.out.println("-- All books --");
            bookService.getAllBooks().forEach(b -> System.out.println(b.toString()));
            System.out.println();
        } catch (EntityNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void borrowSomeBooks(EntityManager em, BorrowerService borrowerService, BookService bookService) {
        System.out.println("-- Borrowing some books --");
        em.getTransaction().begin();

        try {
            Borrower alice = borrowerService.getBorrowerByUsername("alice");
            Borrower bob = borrowerService.getBorrowerByUsername("bob");
            Book b1984 = bookService.getBookByIsbn("9780451524935");
            Book hobbit = bookService.getBookByIsbn("9780547928227");

            b1984.addBorrower(alice);
            b1984.addBorrower(bob);
            bookService.updateBook(b1984);

            hobbit.addBorrower(alice);
            bookService.updateBook(hobbit);

            borrowerService.updateBorrower(alice);
            borrowerService.updateBorrower(bob);
        } catch (EntityNotFoundException | DuplicateEntryException e) {
            em.getTransaction().rollback();
            System.err.println(e.getMessage());
        }

        em.getTransaction().commit();
        System.out.println("-- Borrowing completed. --\n");
    }

    private static void printBooksWithBorrowers(BookService bookService) {
        System.out.println("-- Books with their borrowers --");

        try {
            bookService.getAllBooks().forEach(b -> System.out.println(b.toString()));
        } catch (EntityNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void demonstrateIdempotency(EntityManager em, BorrowerService borrowerService,
                                               BookService bookService) {

        System.out.println("-- Demonstrating idempotent linking (no duplicates) --");
        em.getTransaction().begin();

        try {
            Book b1984 = bookService.getBookByIsbn("9780451524935");
            Borrower alice = borrowerService.getBorrowerByUsername("alice");
            b1984.addBorrower(alice);
            bookService.updateBook(b1984);
        } catch (EntityNotFoundException | DuplicateEntryException e) {
            em.getTransaction().rollback();
            System.err.println(e.getMessage());
        }

        em.getTransaction().commit();
        System.out.println("Re-linked Alice to 1984. Should remain unique.\n");
    }

    private static void removeBorrowerFromBook(EntityManager em, BorrowerService borrowerService,
                                               BookService bookService) {

        System.out.println("-- Removing a borrower from a book (bidirectional update) --");
        em.getTransaction().begin();

        try {
            Book b1984 = bookService.getBookByIsbn("9780451524935");
            Borrower bob = borrowerService.getBorrowerByUsername("bob");
            b1984.removeBorrower(bob);
            bookService.updateBook(b1984);
            borrowerService.updateBorrower(bob);
        } catch (EntityNotFoundException e) {
            em.getTransaction().rollback();
            System.err.println(e.getMessage());
        }

        em.getTransaction().commit();
        System.out.println("Bob returned '1984'.\n");
    }

    private static void printFinalStatus(AuthorService authorService, BookService bookService,
                                          BorrowerService borrowerService) {

        try {
            System.out.println("-- Final status of books, authors and borrowers --");
            authorService.getAllAuthors().forEach(a -> System.out.println(a.toString()));
            System.out.println();
            bookService.getAllBooks().forEach(b -> System.out.println(b.toString()));
            System.out.println();
            borrowerService.getAllBorrowers().forEach(b -> System.out.println(b.toString()));
        } catch (EntityNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

}
