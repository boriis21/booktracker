package com.jpa.booktracker.service;

import com.jpa.booktracker.dao.AuthorDao;
import com.jpa.booktracker.entity.Author;
import com.jpa.booktracker.exception.DuplicateEntryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthorServiceTest extends TestBase {

    private static AuthorService authorService;

    private Author tolkien;
    private Author orwell;

    @BeforeAll
    static void setup() {
        authorService = new AuthorService(new AuthorDao(em));
    }

    @BeforeEach
    void seed() {
        em.getTransaction().begin();
        tolkien = authorService.addAuthor("J.R.R. Tolkien", "Author of LOTR");
        orwell = authorService.addAuthor("George Orwell", "Wrote 1984 and Animal Farm");
        em.getTransaction().commit();
    }

    @AfterEach
    void cleanup() {
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM book_borrower").executeUpdate();
        em.createQuery("DELETE FROM Book").executeUpdate();
        em.createQuery("DELETE FROM Borrower").executeUpdate();
        em.createQuery("DELETE FROM Author").executeUpdate();
        em.getTransaction().commit();
    }

    @Test
    void testAddAuthorCreatesEntity() {
        Author found = authorService.getAuthorByName("J.R.R. Tolkien");

        assertNotNull(found,
            "Expected author to be found in DB");
        assertEquals("Author of LOTR", found.getBio(),
            "Expected author bio to match the given name");
    }

    @Test
    void testAddAuthorThrowsForNullOrEmptyValues() {
        assertThrows(IllegalArgumentException.class, () -> authorService.addAuthor(null, "Bio"),
            "Expected exception to be thrown when trying to add book with null title");

        assertThrows(IllegalArgumentException.class, () -> authorService.addAuthor("Name", null),
            "Expected exception to be thrown when trying to add book with null bio");

        assertThrows(IllegalArgumentException.class, () ->
                authorService.addAuthor("", "Bio"),
            "Expected exception to be thrown when trying to add book with empty title");
    }

    @Test
    void testAddAuthorThatAlreadyExists() {
        assertThrows(DuplicateEntryException.class, () -> authorService.addAuthor("J.R.R. Tolkien", "Bio"),
            "Expected exception to be thrown when trying to add already persisted author");
    }

    @Test
    void testGetAuthorByIdReturnsCorrectEntity() {
        Author byId = authorService.getAuthorById(tolkien.getId());

        assertEquals(tolkien.getName(), byId.getName(),
            "Expected author name to match the author with the given ID");
    }

    @Test
    void testGetAuthorByIdThrowsForNoResult() {
        assertThrows(Exception.class, () -> authorService.getAuthorById(524565L),
            "Expected exception to be thrown when trying to get author with ID that does not exist");
    }

    @Test
    void testGetAuthorByNameReturnsCorrectEntity() {
        Author byName = authorService.getAuthorByName("J.R.R. Tolkien");

        assertEquals(tolkien.getId(), byName.getId(),
            "Expected author ID to match the author with the given name");
    }

    @Test
    void testGetAuthorByNameThrowsForNoResult() {
        assertThrows(Exception.class, () -> authorService.getAuthorByName("Unknown Author"),
            "Expected exception to be thrown when trying to get author with name that does not exist");
    }

    @Test
    void testGetAuthorByNameThrowsForNullOrEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> authorService.getAuthorByName(null),
            "Expected exception to be thrown when trying to get author with null name");

        assertThrows(IllegalArgumentException.class, () -> authorService.getAuthorByName(""),
            "Expected exception to be thrown when trying to get author with empty name");
    }

    @Test
    void testUpdateAuthorUpdatesBio() {
        assertEquals("Author of LOTR", tolkien.getBio(),
            "Expected author bio to be 'Author of LOTR' before the update");

        tolkien.setBio("British author of The Hobbit.");
        em.getTransaction().begin();
        authorService.updateAuthor(tolkien);
        em.getTransaction().commit();

        Author updated = authorService.getAuthorByName("J.R.R. Tolkien");
        assertEquals("British author of The Hobbit.", updated.getBio(),
            "Expected author bio to be 'British author of The Hobbit.' after the update");
    }

    @Test
    void testUpdateAuthorThrowsForNullAuthor() {
        assertThrows(IllegalArgumentException.class, () -> authorService.updateAuthor(null),
            "Expected exception to be thrown when trying to update author with null value");
    }

    @Test
    void testDeleteAuthorRemovesEntity() {
        em.getTransaction().begin();
        authorService.deleteAuthor(orwell);
        em.getTransaction().commit();

        assertThrows(Exception.class, () -> authorService.getAuthorByName("George Orwell"),
            "Expected exception to be thrown when trying to get author after deletion from DB");

        assertEquals(tolkien, authorService.getAuthorByName("J.R.R. Tolkien"),
            "Expected the not deleted author to still be present in the DB");
    }

    @Test
    void testDeleteAuthorThrowsForNonExistingAuthor() {
        assertThrows(Exception.class, () -> authorService.deleteAuthor(new Author()),
            "Expected exception to be thrown when trying to delete author that does not exist");
    }

    @Test
    void testDeleteAuthorThrowsForNullAuthor() {
        assertThrows(IllegalArgumentException.class, () -> authorService.deleteAuthor(null),
            "Expected exception to be thrown when trying to delete author with null value");
    }

}
