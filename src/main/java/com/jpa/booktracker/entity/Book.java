package com.jpa.booktracker.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "books")
public class Book {

    private static final int ISBN_LENGTH = 13;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false, length = ISBN_LENGTH)
    private String isbn;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @ManyToMany
    @JoinTable(
        name = "book_borrower",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "borrower_id"))
    private List<Borrower> borrowers = new ArrayList<>();

    public void setAuthor(Author author) {
        this.author = author;

        if (author != null && !author.getBooks().contains(this)) {
            author.getBooks().add(this);
        }
    }

    public void addBorrower(Borrower borrower) {
        if (borrower == null) {
            throw new IllegalArgumentException("Borrower cannot be null");
        }

        if (!borrowers.contains(borrower)) {
            borrowers.add(borrower);
        }

        if (!borrower.getBorrowedBooks().contains(this)) {
            borrower.getBorrowedBooks().add(this);
        }
    }

    public void removeBorrower(Borrower borrower) {
        if (borrower == null) {
            throw new IllegalArgumentException("Borrower cannot be null");
        }

        if (borrowers.remove(borrower)) {
            borrower.getBorrowedBooks().remove(this);
        }
    }

    @Override
    public String toString() {
        String borrowersNames = (borrowers.isEmpty()) ? "No borrowers" :
            borrowers.stream()
                .map(Borrower::getUsername)
                .collect(Collectors.joining(", "));

        return "Book: " + System.lineSeparator() +
            "Author - " + author.getName() + System.lineSeparator() +
            "Title - " + title + System.lineSeparator() +
            "Genre - " + genre.name() + System.lineSeparator() +
            "ISBN: " + isbn + System.lineSeparator() +
            "Borrowers: " + borrowersNames + System.lineSeparator();
    }

}
