package com.jpa.booktracker.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String bio;

    @OneToMany(mappedBy = "author", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        if (!books.contains(book)) {
            books.add(book);
        }

        if (book.getAuthor() != this) {
            book.setAuthor(this);
        }
    }

    @Override
    public String toString() {
        String booksNames = (books.isEmpty()) ? "No books" :
            books.stream()
                .map(Book::getTitle)
                .collect(Collectors.joining(", "));

        return "Author: " + System.lineSeparator() +
            "Name - " + name + System.lineSeparator() +
            "Bio - " + bio + System.lineSeparator() +
            "Books: " + booksNames + System.lineSeparator();
    }
}
