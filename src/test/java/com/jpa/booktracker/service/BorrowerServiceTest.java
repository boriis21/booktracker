package com.jpa.booktracker.service;

import com.jpa.booktracker.dao.BorrowerDao;
import com.jpa.booktracker.entity.Borrower;
import com.jpa.booktracker.exception.DuplicateEntryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BorrowerServiceTest extends TestBase {

    private static BorrowerService borrowerService;

    private Borrower alice;
    private Borrower bob;

    @BeforeAll
    static void setup() {
        borrowerService = new BorrowerService(new BorrowerDao(em));
    }

    @BeforeEach
    void seed() {
        em.getTransaction().begin();
        alice = borrowerService.addBorrower("alice", "Alice", "Johnson");
        bob = borrowerService.addBorrower("bob", "Bob", "Smith");
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
    void testAddBorrowerPersistsEntity() {
        Borrower borrower = borrowerService.getBorrowerByUsername("alice");

        assertNotNull(borrower,
            "Expected borrower to be found in DB");
        assertEquals("Alice", borrower.getFirstName(),
            "Expected borrower first name to match the given name");
    }

    @Test
    void testAddBorrowerThrowsForNullOrEmptyValues() {
        assertThrows(IllegalArgumentException.class, () ->
                borrowerService.addBorrower(null, "Alice", "Johnson"),
            "Expected exception to be thrown when trying to add book with null username");

        assertThrows(IllegalArgumentException.class, () ->
                borrowerService.addBorrower("alice", null, "Johnson"),
            "Expected exception to be thrown when trying to add book with null first name");

        assertThrows(IllegalArgumentException.class, () ->
                borrowerService.addBorrower("alice", "Alice", null),
            "Expected exception to be thrown when trying to add book with null last name");

        assertThrows(IllegalArgumentException.class, () ->
                borrowerService.addBorrower("", "Alice", "Johnson"),
            "Expected exception to be thrown when trying to add book with empty username");

        assertThrows(IllegalArgumentException.class, () ->
                borrowerService.addBorrower("alice", "", "Johnson"),
            "Expected exception to be thrown when trying to add book with empty first name");

        assertThrows(IllegalArgumentException.class, () ->
                borrowerService.addBorrower("alice", "Alice", ""),
            "Expected exception to be thrown when trying to add book with empty last name");
    }

    @Test
    void testAddBorrowerThatAlreadyExists() {
        assertThrows(DuplicateEntryException.class, () ->
                borrowerService.addBorrower("alice", "Alice", "Johnson"),
            "Expected exception to be thrown when trying to add already persisted borrower");
    }

    @Test
    void testGetBorrowerByIdReturnsEntity() {
        Borrower byId = borrowerService.getBorrowerById(alice.getId());

        assertEquals(alice.getUsername(), byId.getUsername(),
            "Expected borrower username to match the username of the borrower with the given ID");
    }

    @Test
    void testGetBorrowerByIdThrowsForNoResult() {
        assertThrows(Exception.class, () -> borrowerService.getBorrowerById(524565L),
            "Expected exception to be thrown when trying to get borrower with ID that does not exist");
    }

    @Test
    void testGetAllBorrowersReturnsList() {
        List<Borrower> list = borrowerService.getAllBorrowers();

        assertFalse(list.isEmpty(),
            "Expected list of borrowers to be non-empty");
    }

    @Test
    void testGetAllBorrowersThrowsForNoResult() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Borrower").executeUpdate();
        em.getTransaction().commit();

        assertThrows(Exception.class, () -> borrowerService.getAllBorrowers(),
            "Expected exception to be thrown when trying to get all borrowers from empty DB");
    }

    @Test
    void testGetBorrowerByUsernameReturnsCorrectEntity() {
        Borrower byUsername = borrowerService.getBorrowerByUsername("alice");

        assertEquals(alice.getId(), byUsername.getId(),
            "Expected borrower ID to match the borrower with the given username");
    }

    @Test
    void testGetBorrowerByUsernameThrowsForNoResult() {
        assertThrows(Exception.class, () -> borrowerService.getBorrowerByUsername("Unknown Borrower"),
            "Expected exception to be thrown when trying to get borrower with username that does not exist");
    }

    @Test
    void testUpdateBorrowerUpdatesName() {
        assertEquals("Johnson", alice.getLastName(),
            "Expected the last name to be 'Johnson' before the update");

        alice.setLastName("Smith");
        em.getTransaction().begin();
        borrowerService.updateBorrower(alice);
        em.getTransaction().commit();

        Borrower updated = borrowerService.getBorrowerByUsername("alice");
        assertEquals("Smith", updated.getLastName(),
            "Expected the last name to be 'Smith' after the update");
    }

    @Test
    void testUpdateBorrowerThrowsForNullBorrower() {
        assertThrows(IllegalArgumentException.class, () -> borrowerService.updateBorrower(null),
            "Expected exception to be thrown when trying to update borrower with null value");
    }

    @Test
    void testDeleteBorrowerRemovesEntity() {
        Borrower b = borrowerService.getBorrowerByUsername("bob");
        em.getTransaction().begin();
        borrowerService.deleteBorrower(b);
        em.getTransaction().commit();

        assertThrows(Exception.class, () -> borrowerService.getBorrowerByUsername("bob"),
            "Expected exception to be thrown when trying to get borrower after deletion from DB");

        assertEquals(alice, borrowerService.getBorrowerByUsername("alice"),
            "Expected the not deleted borrower to still be present in the DB");
    }

    @Test
    void testDeleteBorrowerThrowsForNonExistingBorrower() {
        assertThrows(Exception.class, () -> borrowerService.deleteBorrower(new Borrower()),
            "Expected exception to be thrown when trying to delete borrower that does not exist");
    }

    @Test
    void testDeleteBorrowerThrowsForNullBorrower() {
        assertThrows(IllegalArgumentException.class, () -> borrowerService.deleteBorrower(null),
            "Expected exception to be thrown when trying to delete borrower with null value");
    }

}
