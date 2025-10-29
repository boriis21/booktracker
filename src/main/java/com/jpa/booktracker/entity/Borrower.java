package com.jpa.booktracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "borrowers")
public class Borrower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToMany(mappedBy = "borrowers")
    private List<Book> borrowedBooks = new ArrayList<>();

    public void borrowBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        if (!borrowedBooks.contains(book)) {
            borrowedBooks.add(book);
        }

        if (!book.getBorrowers().contains(this)) {
            book.getBorrowers().add(this);
        }
    }

    public void returnBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        if (borrowedBooks.remove(book)) {
            book.getBorrowers().remove(this);
        }
    }

    @Override
    public String toString() {
        String borrowedBooksNames = (borrowedBooks == null) ? "No borrowed books" :
            borrowedBooks.stream()
                .map(Book::getTitle)
                .collect(Collectors.joining(", "));

        return "Borrower: " + System.lineSeparator() +
            "Username - " + username + System.lineSeparator() +
            "First name - " + firstName + System.lineSeparator() +
            "Last name - " + lastName + System.lineSeparator() +
            "Borrowed books: " + borrowedBooksNames + System.lineSeparator();
    }

}
