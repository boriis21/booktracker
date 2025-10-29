package com.jpa.booktracker.service;

import com.jpa.booktracker.dao.BookDao;
import com.jpa.booktracker.entity.Author;
import com.jpa.booktracker.entity.Book;
import com.jpa.booktracker.entity.Genre;

import java.util.List;

public class BookService {

    private final BookDao bookDao;

    public BookService(BookDao bookDao) {
        if (bookDao == null) {
            throw new IllegalArgumentException("BookDao cannot be null");
        }

        this.bookDao = bookDao;
    }

    public Book addBook(String title, String isbn, Author author, Genre genre) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }

        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or blank");
        }

        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        if (genre == null) {
            throw new IllegalArgumentException("Genre cannot be null");
        }

        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setGenre(genre);
        author.addBook(book);

        bookDao.addBook(book);
        return book;
    }

    public Book getBookById(Long id) {
        return bookDao.getBookById(id);
    }

    public Book getBookByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or blank");
        }

        return bookDao.getBookByIsbn(isbn);
    }

    public List<Book> getAllBooks() {
        return bookDao.getAllBooks();
    }

    public List<Book> getAllBooksByAuthor(String authorName) {
        if (authorName == null || authorName.isBlank()) {
            throw new IllegalArgumentException("Author name cannot be null or blank");
        }

        return bookDao.getAllBooksByAuthor(authorName);
    }

    public void updateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        bookDao.updateBook(book);
    }

    public void deleteBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        bookDao.deleteBook(book);
    }

}
