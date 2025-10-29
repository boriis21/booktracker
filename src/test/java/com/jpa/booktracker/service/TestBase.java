package com.jpa.booktracker.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class TestBase {

    protected static EntityManagerFactory emf;
    protected static EntityManager em;

    @BeforeAll
    static void initEntityManager() {
        emf = Persistence.createEntityManagerFactory("booktracker");
        em = emf.createEntityManager();
    }

    @AfterAll
    static void closeEntityManager() {
        if (em != null && em.isOpen()) {
            em.close();
        }

        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
