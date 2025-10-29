package com.jpa.booktracker.service;

import com.jpa.booktracker.dao.AuthorDao;
import com.jpa.booktracker.dao.BookDao;
import com.jpa.booktracker.entity.Author;
import com.jpa.booktracker.entity.Book;
import com.jpa.booktracker.entity.Genre;
import com.jpa.booktracker.exception.DuplicateEntryException;
import com.jpa.booktracker.exception.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookServiceTest extends TestBase {

    private static BookService bookService;
    private static AuthorService authorService;

    private Author orwell;
    private Book b1984;
    private Book bAnimalFarm;

    @BeforeAll
    static void setup() {
        authorService = new AuthorService(new AuthorDao(em));
        bookService = new BookService(new BookDao(em));
    }

    @BeforeEach
    void seed() {
        em.clear();
        em.getTransaction().begin();

        orwell = authorService.addAuthor("George Orwell", "Wrote 1984 and Animal Farm");
        b1984 = bookService.addBook("1984", "9780451524935", orwell, Genre.DYSTOPIA);
        bAnimalFarm = bookService.addBook("Animal Farm", "9780451526342", orwell, Genre.DYSTOPIA);

        em.getTransaction().commit();
    }

    @AfterEach
    void cleanup() {
        em.clear();
        em.getTransaction().begin();

        em.createNativeQuery("DELETE FROM book_borrower").executeUpdate();
        em.createQuery("DELETE FROM Book").executeUpdate();
        em.createQuery("DELETE FROM Borrower").executeUpdate();
        em.createQuery("DELETE FROM Author").executeUpdate();

        em.getTransaction().commit();
    }


    @Test
    void testAddBookPersistsEntity() {
        Book found = bookService.getBookByIsbn("9780451524935");

        assertEquals("1984", found.getTitle(),
            "Expected book title to match the book with the given ISBN");
        assertEquals(Genre.DYSTOPIA, found.getGenre(),
            "Expected book genre to match the book with the given ISBN");
    }

    @Test
    void testAddBookThrowsForNullOrEmptyValues() {
        assertThrows(IllegalArgumentException.class, () ->
                bookService.addBook(null, "4829583756214", orwell, Genre.DYSTOPIA),
            "Expected exception to be thrown when trying to add book with null title");

        assertThrows(IllegalArgumentException.class, () ->
                bookService.addBook("title", null, orwell, Genre.DYSTOPIA),
            "Expected exception to be thrown when trying to add book with null ISBN");

        assertThrows(IllegalArgumentException.class, () ->
                bookService.addBook("title", "4829583756214", null, Genre.DYSTOPIA),
            "Expected exception to be thrown when trying to add book with null author");

        assertThrows(IllegalArgumentException.class, () ->
                bookService.addBook("title", "4829583756214", orwell, null),
            "Expected exception to be thrown when trying to add book with null genre");

        assertThrows(IllegalArgumentException.class, () ->
                bookService.addBook("", "4829583756214", orwell, Genre.DYSTOPIA),
            "Expected exception to be thrown when trying to add book with empty title");

        assertThrows(IllegalArgumentException.class, () ->
                bookService.addBook("title", "", orwell, Genre.DYSTOPIA),
            "Expected exception to be thrown when trying to add book with empty ISBN");
    }

    @Test
    void testAddBookThatAlreadyExist() {
        assertThrows(DuplicateEntryException.class, () ->
                bookService.addBook("New Book", "9780451524935", orwell, Genre.FANTASY),
            "Expected exception to be thrown when trying to add already persisted book");
    }

    @Test
    void testGetAllBooksReturnsList() {
        List<Book> books = bookService.getAllBooks();

        assertFalse(books.isEmpty(),
            "Expected list of books to be non-empty");
    }

    @Test
    void testGetAllBooksThrowsForNoResult() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Book").executeUpdate();
        em.getTransaction().commit();

        assertThrows(EntityNotFoundException.class, () -> bookService.getAllBooks(),
            "Expected exception to be thrown when trying to get all books from empty DB");
    }

    @Test
    void testGetBookByIdReturnsAsExpected() {
        Book byId = bookService.getBookById(b1984.getId());

        assertEquals(b1984.getIsbn(), byId.getIsbn(),
            "Expected the given ISBN to match the ISBN of the book with the given ID");
    }

    @Test
    void testGetBookByIdThrowsForNoResult() {
        assertThrows(EntityNotFoundException.class, () -> bookService.getBookById(524565L),
            "Expected exception to be thrown when trying to get book with ID that does not exist");
    }

    @Test
    void testGetBookByIsbnReturnsAsExpected() {
        Book byIsbn = bookService.getBookByIsbn(b1984.getIsbn());

        assertEquals(b1984.getTitle(), byIsbn.getTitle(),
            "Expected the given title to match the title of the book with the given ISBN");
    }

    @Test
    void testGetBookByIsbnThrowsForNoResult() {
        assertThrows(EntityNotFoundException.class, () -> bookService.getBookByIsbn("6739673620564"),
            "Expected exception to be thrown when trying to get book with ID that does not exist");
    }

    @Test
    void testGetBookByIsbnThrowsForNullOrEmptyIsbn() {
        assertThrows(IllegalArgumentException.class, () -> bookService.getBookByIsbn(null),
            "Expected exception to be thrown when trying to get book with null ISBN");

        assertThrows(IllegalArgumentException.class, () -> bookService.getBookByIsbn(""),
            "Expected exception to be thrown when trying to get book with empty ISBN");
    }

    @Test
    void testGetAllBooksByAuthorIdAndNameReturnsList() {
        List<Book> byName = bookService.getAllBooksByAuthor(orwell.getName());

        assertTrue(byName.stream().anyMatch(b -> b.getTitle().equals("1984")),
            "Expected the list of books by name to contain the book with the title '1984'");
    }

    @Test
    void testGetAllBooksByAuthorThrowsForNoResult() {
        assertThrows(EntityNotFoundException.class, () -> bookService.getAllBooksByAuthor("Unknown Author"),
            "Expected exception to be thrown when trying to get books by author that does not exist");
    }

    @Test
    void testGetAllBooksByAuthorThrowsForNullOrEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> bookService.getAllBooksByAuthor(null),
            "Expected exception to be thrown when trying to get books by author with null name");

        assertThrows(IllegalArgumentException.class, () -> bookService.getAllBooksByAuthor(""),
            "Expected exception to be thrown when trying to get books by author with empty name");
    }

    @Test
    void testUpdateBookChangesGenre() {
        assertEquals(Genre.DYSTOPIA, b1984.getGenre(),
            "Expected the genre of the book to be 'DYSTOPIA' before the update");

        b1984.setGenre(Genre.FICTION);
        em.getTransaction().begin();
        bookService.updateBook(b1984);
        em.getTransaction().commit();

        Book updated = bookService.getBookByIsbn("9780451524935");
        assertEquals(Genre.FICTION, updated.getGenre(),
            "Expected the genre of the book to be 'FICTION' after the update");
    }

    @Test
    void testUpdateBookThrowsForNullBook() {
        assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(null),
            "Expected exception to be thrown when trying to update book with null value");
    }

    @Test
    void testDeleteBookRemovesEntity() {
        em.getTransaction().begin();
        bookService.deleteBook(b1984);
        em.getTransaction().commit();

        assertThrows(EntityNotFoundException.class, () -> bookService.getBookByIsbn("9780451524935"),
            "Expected exception to be thrown when trying to get book after deletion from DB");
        assertEquals(bAnimalFarm, bookService.getBookByIsbn("9780451526342"),
            "Expected the not deleted book to still be present in the DB");
    }

    @Test
    void testDeleteBookThrowsForNonExistingBook() {
        assertThrows(EntityNotFoundException.class, () -> bookService.deleteBook(new Book()),
            "Expected exception to be thrown when trying to delete book that does not exist");
    }

    @Test
    void testDeleteBookThrowsForNullBook() {
        assertThrows(IllegalArgumentException.class, () -> bookService.deleteBook(null),
            "Expected exception to be thrown when trying to delete book with null value");
    }

}