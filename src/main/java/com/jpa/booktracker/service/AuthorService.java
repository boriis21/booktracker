package com.jpa.booktracker.service;

import com.jpa.booktracker.dao.AuthorDao;
import com.jpa.booktracker.entity.Author;

import java.util.List;

public class AuthorService {

    private final AuthorDao authorDao;

    public AuthorService(AuthorDao authorDao) {
        if (authorDao == null) {
            throw new IllegalArgumentException("AuthorDao cannot be null");
        }

        this.authorDao = authorDao;
    }

    public Author addAuthor(String name, String bio) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }

        if (bio == null) {
            throw new IllegalArgumentException("Bio cannot be null");
        }

        Author author = new Author();
        author.setName(name);
        author.setBio(bio);

        authorDao.addAuthor(author);
        return author;
    }

    public List<Author> getAllAuthors() {
        return authorDao.getAllAuthors();
    }

    public Author getAuthorById(Long id) {
        return authorDao.getAuthorById(id);
    }

    public Author getAuthorByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }

        return authorDao.getAuthorByName(name);
    }

    public void updateAuthor(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        authorDao.updateAuthor(author);
    }

    public void deleteAuthor(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        authorDao.deleteAuthor(author);
    }

}
