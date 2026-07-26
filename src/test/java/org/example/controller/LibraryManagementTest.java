package org.example.controller;

import java.util.Date;

import org.example.model.Book;
import org.example.model.Member;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibraryManagementTest {

    private LibraryManagement library;
    private Book testBook;
    private Member testMember;

    @BeforeEach
    void setUp() {
        library = LibraryManagement.getInstance();
        library.getBooks().clear();
        library.getMembers().clear();
        testBook = new Book("B000001", "Test Book", "Test Author", 5, "9780062316097");
        testMember = new Member("AD001", "Test Member");
    }

    @AfterEach
    void tearDown() {
        library.getBooks().clear();
        library.getMembers().clear();
    }

    @Test
    void testAddBook() {
        library.addBook(testBook);
        assertEquals(testBook, library.findBookById(testBook.getId()));
    }

    @Test
    void testBorrowBook() {
        library.addBook(testBook);
        library.addMember(testMember);
        int initialQuantity = testBook.getQuantity();
        library.borrowBook(testMember.getId(), testBook.getId(), new Date(), new Date());
        assertEquals(initialQuantity - 1, library.findBookById(testBook.getId()).getQuantity());
    }
}